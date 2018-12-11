package io.mosip.preregistration.batchjobservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwagConfig {
	
	/**
	 * Swagger Configuration
	 */
	@Bean
	public Docket batchStatusBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Pre-Registration-batchjob-Service").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.preregistration.batchjobservices.controller"))
				.paths(PathSelectors.ant("/v0.1/pre-registration/batch/*")).build();
	}

}
