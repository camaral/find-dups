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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author caio.amaral
 *
 */
@Entity
@Table(name = "CUSTOMER_DUPLICATES_LIST")
public class CustomerDuplicatesListJpa {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "CUSTOMER_DUPLICATES_ID", insertable = false, updatable = false)
	private Integer customerDuplicatesId;

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_DUPLICATES_ID", foreignKey = @ForeignKey(name = "FK_DUPLICATES_LIST_TO_DUPLICATES"))
	private CustomerDuplicatesJpa customerDuplicates;

	@Column(name = "DUPLICATE_ID")
	private Integer duplicateId;

	@ManyToOne
	@JoinColumn(name = "DUPLICATE_ID", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "FK_DUPLICATES_LIST_TO_CUSTOMER"))
	private CustomerJpa duplicate;

	@Column(name = "DUPLICATE_PROBABILITY")
	private Integer duplicateProbability;

	/**
	 * @return the customerDuplicates
	 */
	public CustomerDuplicatesJpa getCustomerDuplicates() {
		return customerDuplicates;
	}

	/**
	 * @param customerDuplicates
	 *            the customerDuplicates to set
	 */
	public void setCustomerDuplicates(CustomerDuplicatesJpa customerDuplicates) {
		this.customerDuplicates = customerDuplicates;
	}

	/**
	 * @return the duplicateId
	 */
	public Integer getDuplicateId() {
		return duplicateId;
	}

	/**
	 * @param duplicateId
	 *            the duplicateId to set
	 */
	public void setDuplicateId(Integer duplicateId) {
		this.duplicateId = duplicateId;
	}

	/**
	 * @return the duplicate
	 */
	public CustomerJpa getDuplicate() {
		return duplicate;
	}

	/**
	 * @param duplicate
	 *            the duplicate to set
	 */
	public void setDuplicate(CustomerJpa duplicate) {
		this.duplicate = duplicate;
	}

	/**
	 * @return the duplicateProbability
	 */
	public Integer getDuplicateProbability() {
		return duplicateProbability;
	}

	/**
	 * @param duplicateProbability
	 *            the duplicateProbability to set
	 */
	public void setProbability(Integer duplicateProbability) {
		this.duplicateProbability = duplicateProbability;
	}

}
