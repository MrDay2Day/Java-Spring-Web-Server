package org.file.middleware.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CookieFilter> cookieFilterRegistration(CookieFilter cookieFilter) {
        FilterRegistrationBean<CookieFilter> registration = new FilterRegistrationBean<>(cookieFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }


}