/*
 * Copyright 2011-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package midas.service;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import midas.domain.Customer;
import midas.domain.CustomerDuplicates;
import midas.domain.CustomerDuplicatesIndex;
import midas.domain.CustomerDuplicatesIndexingPage;
import midas.domain.DomainPage;
import midas.domain.IndexStatus;
import midas.entity.jpa.CustomerJpa;
import midas.entity.solr.CustomerSolr;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author caio.amaral
 *
 */
@Service
public class CustomerDuplicatesService extends BaseCustomerService {
	private static final Logger LOGGER = Logger
			.getLogger(CustomerDuplicatesService.class);

	private static final int MAX_DUPLICATES = 5;
	private static final String INDEX_QUEUE_NAME = "qIndexDuplicates";
	private static final int INDEX_PAGE_SIZE = 10;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Transactional(readOnly = true)
	public CustomerDuplicates retrieveDuplicates(final Integer id) {
		final CustomerJpa entity = findEntity(id);
		final Pageable page = new SolrPageRequest(0, MAX_DUPLICATES);

		final Page<CustomerSolr> duplicates = customerSolrRepo
				.findMoreLikeThis(id, entity.getFirstName(),
						entity.getLastName(), page);

		CustomerDuplicates domain = new CustomerDuplicates();
		domain.setDuplicates(mapToDomain(duplicates).getItems());

		return domain;
	}

	@Transactional
	public DomainPage<CustomerDuplicates> retrieveDuplicates(
			final Integer page, final Integer count) {
		final Pageable pageable = new PageRequest(page, count);

		final Page<CustomerJpa> entityPage = customerJpaRepo
				.findDuplicates(pageable);

		return mapToCustomerDuplicates(entityPage);
	}

	@Transactional
	public CustomerDuplicatesIndex indexDuplicates(final Boolean sync) {
		final int count = (int) customerJpaRepo.count();
		final int numPages = (count / INDEX_PAGE_SIZE) + 1;

		for (int page = 0; page < numPages; page++) {
			final CustomerDuplicatesIndexingPage indexPage = new CustomerDuplicatesIndexingPage(
					page, numPages, INDEX_PAGE_SIZE);
			if (Boolean.TRUE.equals(sync)) {
				indexDuplicates(indexPage);
			} else {
				jmsTemplate.send(INDEX_QUEUE_NAME, new MessageCreator() {
					public Message createMessage(Session session)
							throws JMSException {
						return session.createObjectMessage(indexPage);
					}
				});
			}
		}

		return new CustomerDuplicatesIndex(numPages, INDEX_PAGE_SIZE,
				Boolean.TRUE.equals(sync) ? IndexStatus.FINISHED
						: IndexStatus.EXECUTING);
	}

	@Transactional
	@JmsListener(destination = INDEX_QUEUE_NAME)
	public void indexDuplicates(final CustomerDuplicatesIndexingPage page) {
		LOGGER.info("Indexing duplicates for page " + page);

		final Pageable pageable = new PageRequest(page.getPage(),
				page.getCount());

		final Page<CustomerJpa> customers = customerJpaRepo.findAll(pageable);

		for (final CustomerJpa customer : customers) {

			final List<CustomerJpa> duplicates = findDuplicates(customer);
			customer.setDuplicates(duplicates);
			customerJpaRepo.save(customer);
		}
	}

	protected CustomerDuplicates mapToCustomerDuplicates(
			final CustomerJpa entity) {
		return mapper.map(entity, CustomerDuplicates.class);
	}

	private DomainPage<Customer> mapToDomain(final Page<CustomerSolr> documents) {
		final List<Customer> domainList = new ArrayList<>();
		for (CustomerSolr doc : documents) {
			domainList.add(mapper.map(doc, Customer.class));
		}
		return new DomainPage<Customer>(documents.getNumber(),
				documents.getTotalPages(), documents.getSize(), domainList);
	}

	private List<Customer> mapEntitiesToDomain(final List<CustomerJpa> entities) {
		final List<Customer> domainList = new ArrayList<>();
		for (CustomerJpa doc : entities) {
			domainList.add(mapper.map(doc, Customer.class));
		}
		return domainList;
	}

	private DomainPage<CustomerDuplicates> mapToCustomerDuplicates(
			final Page<CustomerJpa> entities) {
		final List<CustomerDuplicates> domainList = new ArrayList<>();
		for (CustomerJpa entity : entities) {
			CustomerDuplicates domain = mapper.map(entity,
					CustomerDuplicates.class);
			domain.setDuplicates(mapEntitiesToDomain(entity.getDuplicates()));
			domainList.add(domain);
		}
		return new DomainPage<CustomerDuplicates>(entities.getNumber(),
				entities.getTotalPages(), entities.getSize(), domainList);
	}

	private List<CustomerJpa> findDuplicates(final CustomerJpa customer) {
		final Pageable solrPage = new SolrPageRequest(0, MAX_DUPLICATES);

		final Page<CustomerSolr> solrResponse = customerSolrRepo
				.findMoreLikeThis(customer.getId(), customer.getFirstName(),
						customer.getLastName(), solrPage);

		final List<CustomerJpa> duplicates = new ArrayList<>();

		for (CustomerSolr customerSolr : solrResponse) {
			CustomerJpa duplicated = customerJpaRepo.findOne(customerSolr
					.getId());
			if (duplicated != null) {
				duplicates.add(duplicated);
			}
		}

		return duplicates;
	}
}
