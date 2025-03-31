//package org.file.middleware.filters;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.*;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.file.apiResponse.ErrorResponse;
//import org.file.utils.GenerateCookie;
//import org.file.utils.JwtUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class AuthFilter implements Filter {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
//
//    private final JwtUtil jwtUtil;
//    private final JwtUtil jwtUtilRefresh;
//
//    @Value("${jwt.cookie.name}")
//    private String jwtCookieName;
//
//    @Value("${jwt.cookie.refresh.name}")
//    private String jwtRefreshCookieName;
//
//    @Value("${jwt.expiration.seconds}")
//    private int jwtExpirationSeconds;
//
//    @Value("${jwt.expiration.refresh.seconds}")
//    private int jwtExpirationRefreshSeconds;
//
//    public AuthFilter(
//            @Value("${jwt.secret}") String secret,
//            @Value("${jwt.refresh.secret}") String secretRefresh,
//            @Value("${jwt.cookie.secret}") String cookieSecret
//    ) {
//        this.jwtUtil = new JwtUtil(secret, cookieSecret);
//        this.jwtUtilRefresh = new JwtUtil(secretRefresh, cookieSecret);
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        try{
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        Cookie[] cookies = httpRequest.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(jwtCookieName)) {
//                    String token = new GenerateCookie(jwtUtil).verifyCookie(cookie);
//
//                    if (!jwtUtil.isTokenExpired(token)) {
//                        String username = jwtUtil.extractClaim(token, "username");
//                        String role = jwtUtil.extractClaim(token, "role");
//
//                        Map<String, String> userData = new HashMap<>();
//                        userData.put("username", username);
//                        userData.put("role", role);
//
//                        // Set user data in request attributes for later use in controllers
//                        request.setAttribute("userData", userData);
//                        chain.doFilter(request, response); // Allow request to proceed
//                        return;
//                    }
//                    break;
//                }
//            }
//
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(jwtRefreshCookieName)) {
//                    String tokenRefresh = new GenerateCookie(jwtUtilRefresh).verifyCookie(cookie);
//                    if (tokenRefresh == null || jwtUtilRefresh.isTokenExpired(tokenRefresh)) {
//                        ErrorResponse.sendErrorResponse(httpResponse, HttpStatus.BAD_REQUEST, "Unauthorized");
//                        return;
//                    }
//                    Claims oldClaims = jwtUtilRefresh.extractAllClaims(tokenRefresh);
//
//                    GenerateCookie generateJwtCookie = new GenerateCookie(
//                            jwtCookieName, jwtExpirationSeconds, (Map<String, Object>) oldClaims,
//                            (HttpServletRequest) response,
//                            jwtUtil);
//                    String newToken = generateJwtCookie.generateToken();
//                    oldClaims.put("jwt", newToken);
//                    GenerateCookie generateRefreshJwtCookie = new GenerateCookie(
//                            jwtRefreshCookieName, jwtExpirationRefreshSeconds, (Map<String, Object>) oldClaims,
//                            (HttpServletRequest) response,
//                            jwtUtilRefresh);
//
//                    generateRefreshJwtCookie.generateToken();
//
//                    String username = jwtUtil.extractClaim(newToken, "username");
//                    String role = jwtUtil.extractClaim(newToken, "role");
//
//                    Map<String, String> userData = new HashMap<>();
//                    userData.put("username", username);
//                    userData.put("role", role);
//
//                    request.setAttribute("userData", userData);
//                    chain.doFilter(request, response);
//                    return;
//                }
//            }
//        }
//
//        // If no valid tokens found, send unauthorized response
//        ErrorResponse.sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED, "Unauthorized.");
//    } catch (Exception e) {
//            logger.error("Error in AuthFilter: ", e);
//            ErrorResponse.sendErrorResponse((HttpServletResponse) response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
//        }
//    }
//
//}
//
