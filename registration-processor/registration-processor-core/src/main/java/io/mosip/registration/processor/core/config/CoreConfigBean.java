package io.mosip.registration.processor.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.core.notification.template.mapping.RegistrationProcessorNotificationTemplate;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.queue.factory.MosipQueueConnectionFactoryImpl;
import io.mosip.registration.processor.core.queue.impl.MosipActiveMqImpl;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;

@Configuration
public class CoreConfigBean {

	@Bean
	public RegistrationProcessorIdentity getRegProcessorIdentityJson() {
		return new RegistrationProcessorIdentity();
	}

	@Bean
	RegistrationProcessorNotificationTemplate getRegistrationProcessorNotificationTemplate() {
		return new RegistrationProcessorNotificationTemplate();
	}
	
	@Bean
	MosipQueueManager<?, ?> getMosipQueueManager(){
		return new MosipActiveMqImpl();
	}
	
	@Bean
	MosipQueueConnectionFactory<?> getMosipQueueConnectionFactory(){
		return new MosipQueueConnectionFactoryImpl();
	}
}
