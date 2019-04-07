package io.mosip.kyc.authentication.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kyc.authentication.service.filter.KycAuthFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */
@Configuration
public class KycFilterConfig {

	@Value("${ida.api.version}")
	private String apiVersion;

	/**
	 * Gets the eKyc filter.
	 *
	 * @return the eKyc filter
	 */
	@Bean
	public FilterRegistrationBean<KycAuthFilter> getEkycFilter() {
		FilterRegistrationBean<KycAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new KycAuthFilter());
		registrationBean.addUrlPatterns("/kyc/" + apiVersion + "/*");
		return registrationBean;
	}

}
