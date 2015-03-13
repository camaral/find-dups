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
package midas.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import midas.entity.jpa.CustomerJpa;
import midas.testcategory.TryOut;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author caio.amaral
 *
 */
@Category(TryOut.class)
public class TestDerbySetup {

	@Test
	public void testSetup() {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("test");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		CustomerJpa test = em.find(CustomerJpa.class, 1);
		if (test == null) {
			test = new CustomerJpa();
			test.setFirstName("caio");
			test.setLastName("amaral");

			tx.begin();
			em.persist(test);
			tx.commit();

			test = em.find(CustomerJpa.class, 1);
		}

		System.out.format("Test{id=%s, name=%s %s}\n", test.getId(),
				test.getFirstName(), test.getLastName());

		em.close();
		emf.close();
	}
}
