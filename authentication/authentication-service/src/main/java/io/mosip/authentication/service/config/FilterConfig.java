package io.mosip.authentication.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.service.filter.DefaultIDAFilter;
import io.mosip.authentication.service.filter.IdAuthFilter;
import io.mosip.authentication.service.filter.InternalAuthFilter;
import io.mosip.authentication.service.filter.KycAuthFilter;
import io.mosip.authentication.service.filter.OTPFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */
@Configuration
public class FilterConfig {

    /**
     * Gets the otp filter.
     *
     * @return the otp filter
     */
    @Bean
    public FilterRegistrationBean<OTPFilter> getOtpFilter() {
	FilterRegistrationBean<OTPFilter> registrationBean = new FilterRegistrationBean<>();
	registrationBean.setFilter(new OTPFilter());
	registrationBean.addUrlPatterns("/otp/0.8/*");

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
	registrationBean.addUrlPatterns("/auth/0.8/*");

	return registrationBean;
    }

    /**
     * Gets the eKyc filter.
     *
     * @return the eKyc filter
     */
    @Bean
    public FilterRegistrationBean<KycAuthFilter> getEkycFilter() {
	FilterRegistrationBean<KycAuthFilter> registrationBean = new FilterRegistrationBean<>();
	registrationBean.setFilter(new KycAuthFilter());
	registrationBean.addUrlPatterns("/kyc/0.8/*");

	return registrationBean;
    }
    
    
    /**
     * Gets the internal auth filter.
     *
     * @return the internal auth filter
     */
    @Bean
    public FilterRegistrationBean<InternalAuthFilter> getInternalAuthFilter() {
	FilterRegistrationBean<InternalAuthFilter> registrationBean = new FilterRegistrationBean<>();
	registrationBean.setFilter(new InternalAuthFilter());
	registrationBean.addUrlPatterns("/auth/internal/0.8/*");

	return registrationBean;
    }
   
    /**
     * Gets the Static Pin Store Filter.
     *
     * @return Static Pin Store Filter
     */
    @Bean
    public FilterRegistrationBean<DefaultIDAFilter> getStaticPinStoreFilter() {
	FilterRegistrationBean<DefaultIDAFilter> registrationBean = new FilterRegistrationBean<>();
	registrationBean.setFilter(new DefaultIDAFilter());
	registrationBean.addUrlPatterns("/staticpin/0.8","/vid/0.8/*");

	return registrationBean;
    }

}
