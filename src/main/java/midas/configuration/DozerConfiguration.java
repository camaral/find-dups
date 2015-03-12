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
package midas.configuration;

import midas.domain.Customer;
import midas.entity.CustomerEntity;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caio.amaral
 *
 */
@Configuration
public class DozerConfiguration {
	@Bean
	public Mapper customerMapper() {
		final BeanMappingBuilder builder = new BeanMappingBuilder() {
			protected void configure() {
				mapping(Customer.class, CustomerEntity.class,
						TypeMappingOptions.oneWay(),
						TypeMappingOptions.mapNull(true)).exclude("id");

				mapping(CustomerEntity.class, Customer.class,
						TypeMappingOptions.oneWay(),
						TypeMappingOptions.mapNull(true));
			}
		};

		final DozerBeanMapper mapper = new DozerBeanMapper();
		mapper.addMapping(builder);

		return mapper;
	}
}
