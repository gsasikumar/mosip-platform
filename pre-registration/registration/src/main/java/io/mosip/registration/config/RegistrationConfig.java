package io.mosip.registration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class RegistrationConfig {

	@Bean
	public Docket registrationStatusBean() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Registration").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.registration.controller"))
				.paths(PathSelectors.ant("/v0.1/pre-registration/registration/*")).build();
	}

}
