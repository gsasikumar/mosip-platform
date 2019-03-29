package io.mosip.kernel.auth.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Raj Jha
 * 
 * @since 1.0.0
 *
 */
@Configuration
public class Config {


	@Bean(name="CorsFilter")
	public FilterRegistrationBean<Filter> registerCORSFilterBean() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(registerCORSFilter());
		corsBean.setOrder(0);
		return corsBean;
	}
	
	@Bean(name="ReqResponseFilter")
	public FilterRegistrationBean<Filter> registerReqResFilterBean() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(getReqResFilter());
		corsBean.setOrder(1);
		return corsBean;
	}

	@Bean
	public Filter registerCORSFilter() {
		return new CorsFilter();
	}
	
	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}


}
