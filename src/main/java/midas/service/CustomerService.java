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

import midas.domain.Customer;
import midas.entity.jpa.CustomerJpa;
import midas.entity.solr.CustomerSolr;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author caio.amaral
 *
 */
@Service
public class CustomerService extends BaseCustomerService {

	@Transactional
	public Customer create(final Customer customer) {
		final CustomerJpa entity = mapToEntity(customer);
		return save(entity);
	}

	@Transactional(readOnly = true)
	public Customer retrieve(final Integer id) {
		return find(id);
	}

	@Transactional
	public Customer update(final Integer id, final Customer customer) {
		final CustomerJpa entity = findEntity(id);
		mapToEntity(customer, entity);
		return save(entity);
	}

	@Transactional
	public Customer delete(final Integer id) {
		final Customer domain = find(id);
		customerJpaRepo.delete(id);
		customerSolrRepo.delete(id);
		return domain;
	}

	@Transactional
	public void deleteAll() {
		customerJpaRepo.deleteAll();
		customerSolrRepo.deleteAll();
	}

	private Customer save(final CustomerJpa entity) {
		final CustomerJpa savedEntity = customerJpaRepo.save(entity);

		final Customer domain = mapToDomain(savedEntity);

		final CustomerSolr document = mapToSolr(domain);
		customerSolrRepo.save(document);

		return domain;
	}
}