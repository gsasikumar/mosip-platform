package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class RestClientConfig {
	@Bean
	public Docket packetUploaderApis() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Rest-Client").select()
				.apis(RequestHandlerSelectors
						.basePackage("io.mosip.registrationprocessor.mosip_regprocessor_rest_client.controller"))
				.paths(PathSelectors.ant("/v0.1/registration-processor/rest-client/*")).build();
	}

}
