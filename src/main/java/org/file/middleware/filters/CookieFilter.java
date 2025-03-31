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
            logger.info("Header: {} = {}", headerName, headerValue);
        }

        // Accessing cookies
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).forEach(cookie ->
                    logger.info("Cookie: {} = {}", cookie.getName(), cookie.getValue())
            );
        }

        chain.doFilter(request, response);
        return;
    }
}