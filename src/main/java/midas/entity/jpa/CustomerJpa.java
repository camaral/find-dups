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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * @author caio.amaral
 *
 */
@Entity
@Table(name = "CUSTOMERS")
public class CustomerJpa {
	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "FIRST_NAME")
	private String firstName;
	@Column(name = "LAST_NAME")
	private String lastName;

	@JoinTable(name = "CUSTOMER_DUPLICATES", //
	joinColumns = { @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "id", nullable = false) }, //
	inverseJoinColumns = { @JoinColumn(name = "DUPLICATE_ID", referencedColumnName = "id", nullable = false) })
	@ManyToMany
	private List<CustomerJpa> duplicates;

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
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @param duplicates
	 *            the duplicates to set
	 */
	public void setDuplicates(List<CustomerJpa> duplicates) {
		this.duplicates = duplicates;
	}

	/**
	 * @return the duplicates
	 */
	public List<CustomerJpa> getDuplicates() {
		return duplicates;
	}

	@Override
	public String toString() {
		return "CustomerJpa [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + "]";
	}

}
