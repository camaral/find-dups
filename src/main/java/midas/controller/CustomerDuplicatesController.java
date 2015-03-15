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
package midas.controller;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import midas.domain.Customer;
import midas.domain.CustomerDuplicatesIndexingPage;
import midas.domain.DomainPage;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author caio.amaral
 *
 */
@Controller
public class CustomerDuplicatesController extends BaseCustomerController {
	private static final Logger LOGGER = Logger
			.getLogger(CustomerDuplicatesController.class);

	private static final int MAX_DUPLICATES = 5;
	private static final String INDEX_QUEUE_NAME = "qIndexDuplicates";
	private static final int INDEX_PAGE_SIZE = 10;

	@Autowired
	public JmsTemplate jmsTemplate;

	@Transactional(readOnly = true)
	public DomainPage<Customer> retrieveDuplicates(final Integer id) {
		final CustomerJpa entity = findEntity(id);
		final Pageable page = new SolrPageRequest(0, MAX_DUPLICATES);

		final Page<CustomerSolr> duplicates = customerSolrRepo
				.findMoreLikeThis(id, entity.getFirstName(),
						entity.getLastName(), page);

		return mapToDomain(duplicates);
	}

	public void indexDuplicates() {
		final int count = (int) customerJpaRepo.count();
		final int numPages = (count / INDEX_PAGE_SIZE) + 1;

		for (int page = 0; page < numPages; page++) {
			final CustomerDuplicatesIndexingPage indexPage = new CustomerDuplicatesIndexingPage(
					page, numPages, INDEX_PAGE_SIZE);

			jmsTemplate.send(INDEX_QUEUE_NAME, new MessageCreator() {
				public Message createMessage(Session session)
						throws JMSException {
					return session.createObjectMessage(indexPage);
				}
			});
		}
	}

	@JmsListener(destination = INDEX_QUEUE_NAME)
	public void indexDuplicates(final CustomerDuplicatesIndexingPage page) {
		LOGGER.info("Indexing duplicates for page " + page);

		final Pageable pageable = new PageRequest(page.getPage(),
				page.getCount());

		final Page<CustomerJpa> customers = customerJpaRepo.findAll(pageable);

		for (final CustomerJpa customer : customers) {
			final Pageable solrPage = new SolrPageRequest(0, MAX_DUPLICATES);

			final Page<CustomerSolr> duplicates = customerSolrRepo
					.findMoreLikeThis(customer.getId(),
							customer.getFirstName(), customer.getLastName(),
							solrPage);

		}
	}

}
