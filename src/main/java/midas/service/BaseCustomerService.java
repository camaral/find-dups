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

import javax.ws.rs.NotFoundException;

import midas.domain.Customer;
import midas.entity.jpa.CustomerJpa;
import midas.entity.solr.CustomerSolr;
import midas.repository.jpa.CustomerJpaRepository;
import midas.repository.solr.CustomerSolrRepository;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author caio.amaral
 *
 */
public class BaseCustomerService {

	@Autowired
	protected CustomerJpaRepository customerJpaRepo;

	@Autowired
	protected CustomerSolrRepository customerSolrRepo;

	@Autowired
	@Qualifier("customerMapper")
	protected Mapper mapper;

	protected CustomerJpa findEntity(final Integer id) {
		final CustomerJpa entity = customerJpaRepo.findOne(id);
		if (entity == null) {
			throw new NotFoundException();
		}
		return entity;
	}

	protected Customer find(final Integer id) {
		final CustomerJpa entity = customerJpaRepo.findOne(id);
		if (entity == null) {
			throw new NotFoundException();
		}
		return mapToDomain(entity);
	}

	protected CustomerJpa mapToEntity(final Customer domain) {
		return mapper.map(domain, CustomerJpa.class);
	}

	protected void mapToEntity(final Customer domain, final CustomerJpa entity) {
		mapper.map(domain, entity);
	}

	protected Customer mapToDomain(final CustomerJpa entity) {
		return mapper.map(entity, Customer.class);
	}

	protected CustomerSolr mapToSolr(final Customer domain) {
		return mapper.map(domain, CustomerSolr.class);
	}
}
