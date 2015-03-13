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

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import midas.domain.Customer;
import midas.entity.jpa.CustomerJpa;
import midas.entity.solr.CustomerSolr;
import midas.repository.jpa.CustomerJpaRepository;
import midas.repository.solr.CustomerSolrRepository;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

/**
 * @author caio.amaral
 *
 */
@Controller
public class CustomerController {

	@Autowired
	private CustomerJpaRepository customerJpaRepo;

	@Autowired
	private CustomerSolrRepository customerSolrRepo;

	@Autowired
	@Qualifier("customerMapper")
	private Mapper mapper;

	public Customer create(final Customer customer) {
		CustomerJpa entity = mapToEntity(customer);
		entity = customerJpaRepo.save(entity);

		final Customer domain = mapToDomain(entity);

		final CustomerSolr document = mapToSolr(domain);
		customerSolrRepo.save(document);

		return domain;
	}

	@Transactional
	public Customer retrieve(final Integer id) {
		final CustomerJpa entity = findEntity(id);
		return mapToDomain(entity);
	}

	@Transactional
	public Customer update(final Integer id, final Customer customer) {
		final CustomerJpa entity = findEntity(id);
		mapToEntity(customer, entity);
		customerJpaRepo.save(entity);
		return mapToDomain(entity);
	}

	@Transactional
	public Customer delete(final Integer id) {
		final CustomerJpa entity = findEntity(id);
		customerJpaRepo.delete(id);
		return mapToDomain(entity);
	}

	private CustomerJpa findEntity(final Integer id) {
		final CustomerJpa entity = customerJpaRepo.findOne(id);
		if (entity == null) {
			throw new NotFoundException();
		}
		return entity;
	}

	private CustomerJpa mapToEntity(final Customer domain) {
		return mapper.map(domain, CustomerJpa.class);
	}

	private void mapToEntity(final Customer domain, final CustomerJpa entity) {
		mapper.map(domain, entity);
	}

	private Customer mapToDomain(final CustomerJpa entity) {
		return mapper.map(entity, Customer.class);
	}

	private CustomerSolr mapToSolr(final Customer domain) {
		return mapper.map(domain, CustomerSolr.class);
	}
}
