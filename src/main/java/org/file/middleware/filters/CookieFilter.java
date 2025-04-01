package org.file.middleware.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;


/*
     Filters:

     Servlet API Level: Filters are part of the Servlet API and operate at a lower level than interceptors. They intercept requests and responses before they reach the Spring DispatcherServlet.
     No Access to Handler: Filters don't have direct access to the Spring MVC handler.
     Use Cases: Logging, security, character encoding, request/response modification, and tasks that are not specific to Spring MVC.

     Filter Registration (If needed):

     In most cases, Spring Boot automatically registers filters annotated with @Component.
     If you need finer control over the filter's order or URL patterns, you can register it programmatically:

     @Configuration
     public class FilterConfig {

         @Bean
         public FilterRegistrationBean<MyFilter> myFilterRegistration(MyFilter myFilter) {
             FilterRegistrationBean<MyFilter> registration = new FilterRegistrationBean<>(myFilter);
             registration.addUrlPatterns("/*"); // Apply to all URLs, or specify specific ones
             registration.setOrder(1); // Set the filter's order
             return registration;
         }
     }
 */
@Component
public class CookieFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CookieFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Accessing headers
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = httpRequest.getHeader(headerName);
            logger.info("Filter -> Header: {}", headerName);
        }

        // Accessing cookies
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).forEach(cookie ->
                    logger.info("Filter -> Cookie: {}", cookie.getName())
            );
        }

        chain.doFilter(request, response);
        return;
    }
}