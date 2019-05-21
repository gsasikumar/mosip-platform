package io.mosip.registrationprocessor.eis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * external Configuration
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	/**
	 * DummyBean method for swagger configuration
	 * @return
	 */
	@Bean
	public Docket DummyBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("external-integration-service").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.registrationprocessor.eis.controller"))
				.paths(PathSelectors.ant("/registration-processor/*/*")).build();
	}

}
