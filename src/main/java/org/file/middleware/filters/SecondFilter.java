package org.file.middleware.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SecondFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SecondFilter.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludedPaths = Arrays.asList("/public/*", "/login", "/health");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        boolean shouldFilter = true;
        for (String excludedPath : excludedPaths) {
            if (pathMatcher.match(excludedPath, requestURI)) {
                shouldFilter = false;
                break;
            }
        }

        if (shouldFilter) {
            logger.info("Filter - Request: {} {}", httpRequest.getMethod(), requestURI);

            // Perform filtering logic here

            chain.doFilter(request, response);
        } else {
            chain.doFilter(request, response); // Bypass filter logic
        }
        return;
    }
}