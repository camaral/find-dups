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
package midas.entity.jpa;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author caio.amaral
 *
 */
@Entity
@Table(name = "CUSTOMER_DUPLICATES")
public class CustomerDuplicatesJpa {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "CUSTOMER_ID")
	private Integer customerId;

	@OneToOne
	@JoinColumn(name = "CUSTOMER_ID", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "FK_DUPLICATES_TO_CUSTOMER"))
	private CustomerJpa customer;

	@Column(name = "HIGHER_DUPLICATE_PROBABILITY")
	private Integer higherDuplicateProbability;

	@OneToMany(mappedBy = "customerDuplicates", cascade = CascadeType.ALL)
	private List<CustomerDuplicatesListJpa> duplicates;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId
	 *            the customerId to set
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the customer
	 */
	public CustomerJpa getCustomer() {
		return customer;
	}

	/**
	 * @param customer
	 *            the customer to set
	 */
	public void setCustomer(CustomerJpa customer) {
		this.customer = customer;
	}

	/**
	 * @return the higherDuplicateProbability
	 */
	public Integer getHigherDuplicateProbability() {
		return higherDuplicateProbability;
	}

	/**
	 * @param higherDuplicateProbability
	 *            the higherDuplicateProbability to set
	 */
	public void setHigherDuplicateProbability(Integer higherDuplicateProbability) {
		this.higherDuplicateProbability = higherDuplicateProbability;
	}

	/**
	 * @return the duplicates
	 */
	public List<CustomerDuplicatesListJpa> getDuplicates() {
		return duplicates;
	}

	/**
	 * @param duplicates
	 *            the duplicates to set
	 */
	public void setDuplicates(List<CustomerDuplicatesListJpa> duplicates) {
		this.duplicates = duplicates;
	}

}
