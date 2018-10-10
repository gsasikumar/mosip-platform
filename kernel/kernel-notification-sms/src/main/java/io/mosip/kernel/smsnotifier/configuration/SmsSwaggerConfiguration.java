package io.mosip.kernel.smsnotifier.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.smsnotifier.constant.SmsPropertyConstants;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration class for swagger config
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Configuration
@EnableSwagger2
public class SmsSwaggerConfiguration {
	/**
	 * Produce Docket bean
	 * 
	 * @return Docket bean
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).groupName(SmsPropertyConstants.PROJECT_NAME.getProperty())
				.select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
	}
}
