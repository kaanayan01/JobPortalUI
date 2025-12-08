package com.jobportal.config;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jobportal.filter.JwtFilter;
import com.jobportal.service.JwtService;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtService jwtService) {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilter(jwtService));
        registrationBean.addUrlPatterns("/*"); // filter all URLs
        registrationBean.setOrder(1);          // run early
        return registrationBean;
    }
}
