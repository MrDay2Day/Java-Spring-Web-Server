package org.file.middleware.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FirstFilter> firstFilterRegistration(FirstFilter myFirstFilter) {
        FilterRegistrationBean<FirstFilter> registration = new FilterRegistrationBean<>(myFirstFilter);
        registration.addUrlPatterns("/*"); // Apply to all URLs
        registration.setOrder(1); // First filter
        return registration;
    }

    /* Adding Additional Filters */

     @Bean
     public FilterRegistrationBean<SecondFilter> secondFilterRegistration(SecondFilter secondFilter) {
         FilterRegistrationBean<SecondFilter> registration = new FilterRegistrationBean<>(secondFilter);
         registration.addUrlPatterns("/*");
         registration.setOrder(2);
         return registration;
     }

    @Bean
    public FilterRegistrationBean<CookieFilter> cookieFilterRegistration(CookieFilter cookieFilter) {
        FilterRegistrationBean<CookieFilter> registration = new FilterRegistrationBean<>(cookieFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }


}