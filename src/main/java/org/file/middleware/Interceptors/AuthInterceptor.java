package org.file.middleware.Interceptors;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.file.apiResponse.HttpServletErrorResponse;
import org.file.utils.GenerateCookie;
import org.file.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

/**
 * In Spring Boot, middleware is implemented using interceptors and filters. Both allow you to intercept and process HTTP requests and responses, but they operate at different levels of the Spring application lifecycle.
 *
 * Interceptors:
 *
 * Spring MVC Specific: Interceptors are part of the Spring MVC framework. They're typically used for tasks that relate to handling web requests within the Spring MVC context.
 * Access to Handler: Interceptors have access to the handler (the controller method) that will process the request.
 * Phases of Execution: They can intercept requests before, during, and after the handler's execution.
 * Use Cases: Authentication, authorization, logging, request/response modification, and adding common headers.
 */

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    private final JwtUtil jwtUtil;
    private final JwtUtil jwtUtilRefresh;
    @Value("${jwt.cookie.name}") // Set this in application.properties or application.yml
    private String jwtCookieName;

    @Value("${jwt.cookie.refresh.name}") // Set this in application.properties or application.yml
    private String jwtRefreshCookieName;

    @Value("${jwt.expiration.seconds}") // Set this in application.properties or application.yml
    private int jwtExpirationSeconds;

    @Value("${jwt.expiration.refresh.seconds}") // Set this in application.properties or application.yml
    private int jwtExpirationRefreshSeconds;
    @Value("${jwt.cookie.secret}")
    private String jwtCookieSecret;


    public AuthInterceptor(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.refresh.secret}") String secretRefresh,
            @Value("${jwt.cookie.secret}") String cookieSecret
    ) {
        this.jwtUtil = new JwtUtil(secret, cookieSecret);
        this.jwtUtilRefresh = new JwtUtil(secretRefresh, cookieSecret);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("PreHandle - Request: {} {}", request.getMethod(), request.getRequestURI());

        // Accessing headers
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            logger.info("Header: {} = {}", headerName, headerValue);
        }

        // Accessing cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // Search for AUTH Token
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtCookieName)) {
                    String token = new GenerateCookie(jwtUtil).verifyCookie(cookie);
                    if (!jwtUtil.isTokenExpired(token)) {
                        return true;
                    }
                    break;
                }
            }

            // Search for Refresh Token to create new AUTH token
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtRefreshCookieName)) {
                    String tokenRefresh = new GenerateCookie(jwtUtilRefresh).verifyCookie(cookie);
                    if (tokenRefresh == null || jwtUtilRefresh.isTokenExpired(tokenRefresh)) {
                        HttpServletErrorResponse.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorised");
                        return false;
                    }
                    Claims oldClaims = jwtUtilRefresh.extractAllClaims(tokenRefresh);

                    GenerateCookie generateJwtCookie = new GenerateCookie(
                            jwtCookieName, jwtExpirationSeconds, oldClaims,
                            response,
                            jwtUtil);
                    String newToken = generateJwtCookie.generateToken();
                    oldClaims.put("jwt", newToken);
                    GenerateCookie generateRefreshJwtCookie = new GenerateCookie(
                            jwtRefreshCookieName, jwtExpirationRefreshSeconds, oldClaims,
                            response,
                            jwtUtilRefresh);
                    System.err.println("\n\n\nInterceptor newToken: " + newToken);
                    String newTokenRefresh = generateRefreshJwtCookie.generateToken();
                    System.out.println("\n\nInterceptor Refresh Successfully. \n\n" +
                            "AuthToken: " + newToken + "\n" +
                            "Refresh Token: " + newTokenRefresh);
                    return true;
                }
            }
        }
        HttpServletErrorResponse.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorised");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("PostHandle - Response status: {}", response.getStatus());
        // Perform post-processing here
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            logger.error("AfterCompletion - Exception: {}", ex.getMessage());
        }
        // Perform cleanup or logging after the request is complete
    }
}