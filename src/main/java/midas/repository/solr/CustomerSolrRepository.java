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
package midas.repository.solr;

import midas.entity.solr.CustomerSolr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * @author caio.amaral
 *
 */
public interface CustomerSolrRepository extends
		SolrCrudRepository<CustomerSolr, Integer> {

	@Query(value = "?0&mlt.fl=last_name_s,first_name_s&mlt.mindf=1&mlt.mintf=1")
	Page<CustomerSolr> findMoreLikeThis(String name, Pageable page);

	@Query(value = "(?1 ?2) AND -id:?0")
	//&mlt.fl=last_name_s,first_name_s&mlt.mindf=1&mlt.mintf=1
	Page<CustomerSolr> findMoreLikeThis(Integer id, String firstName,
			String lastName, Pageable page);
}
