package io.mosip.authentication.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.service.filter.IdAuthFilter;
import io.mosip.authentication.service.filter.OTPFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */
@Configuration
public class FilterConfig {
	
	@Bean
	public FilterRegistrationBean<OTPFilter> getOtpFilter() {
		FilterRegistrationBean<OTPFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new OTPFilter());
		registrationBean.addUrlPatterns("/otp");

		return registrationBean;
	}

	/**
	 * Gets the auth filter.
	 *
	 * @return the auth filter
	 */
	@Bean
	public FilterRegistrationBean<IdAuthFilter> getIdAuthFilter() {
		FilterRegistrationBean<IdAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new IdAuthFilter());
		registrationBean.addUrlPatterns("/auth");

		return registrationBean;
	}

}
