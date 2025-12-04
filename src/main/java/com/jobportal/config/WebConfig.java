package com.jobportal.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jobportal.filter.CustomCorsFilter;
import com.jobportal.interceptor.RoleInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final RoleInterceptor roleInterceptor;

	public WebConfig(RoleInterceptor roleInterceptor) {
		this.roleInterceptor = roleInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(roleInterceptor).addPathPatterns("/**"); // apply to all paths
	}

	@Autowired
	private CustomCorsFilter customCorsFilter;

	@Bean
	FilterRegistrationBean<CustomCorsFilter> corsFilterRegistration() {
		FilterRegistrationBean<CustomCorsFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(customCorsFilter);
		registration.addUrlPatterns("/*");
		registration.setOrder(0); // ensure this runs before Spring Security filters
		return registration;
	}

}
