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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * @author caio.amaral
 *
 */

@Configuration
public class JmsConfiguration {

	@Bean
	public BrokerService activeMqBroker() throws Exception {
		BrokerService broker = new BrokerService();

		broker.setBrokerName("localhost");
		broker.start();

		return broker;
	}

	@Bean
	public ActiveMQConnectionFactory jmsConnectionFactory() {
		return new ActiveMQConnectionFactory("vm://localhost?create=false");
	}

	@Bean
	public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory() {
		final DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
		containerFactory.setConnectionFactory(jmsConnectionFactory());
		return containerFactory;
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		final JmsTemplate template = new JmsTemplate(jmsConnectionFactory());
		return template;
	}

}