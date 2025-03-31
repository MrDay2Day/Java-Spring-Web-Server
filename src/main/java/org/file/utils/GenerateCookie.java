package org.file.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;


public class GenerateCookie {

    @Value("${jwt.cookie.name}")
    private String jwtCookieName;

    @Value("${jwt.cookie.refresh.name}")
    private String jwtRefreshCookieName;

    @Value("${jwt.expiration.minutes}")
    private int jwtExpirationMinutes;

    @Value("${jwt.expiration.refresh.minutes}")
    private int jwtExpirationRefreshMinutes;

    @Value("${jwt.cookie.secret}") // Add this property to your configuration
    private String jwtCookieSecret;

    private Map<String, Object> claim;
    private int expTime;
    private String cookieName;
    private HttpServletResponse response;
    private jakarta.servlet.http.HttpServletRequest request;
    private JwtUtil jwtUtil;


    public  GenerateCookie(JwtUtil _jwtUtil){
        this.jwtUtil = _jwtUtil;
    };

    public GenerateCookie(String _cookieName, int _expTime,
                          Map<String, Object> _claim,
                          jakarta.servlet.http.HttpServletRequest _request,
                          JwtUtil _jwtUtil) {
        this.claim = _claim;
        this.cookieName = _cookieName;
        this.expTime = _expTime;
        this.request = _request;
        this.jwtUtil = _jwtUtil;
    }

    public GenerateCookie(String _cookieName, int _expTime, Map<String,
                                  Object> _claim, HttpServletResponse _response,
                          JwtUtil _jwtUtil) {
        this.claim = _claim;
        this.cookieName = _cookieName;
        this.expTime = _expTime;
        this.response = _response;
        this.jwtUtil = _jwtUtil;
    }

    public static void clearCookie(HttpServletResponse response, String jwtCookieName) {
        Cookie cookie = new Cookie(jwtCookieName, null);
        cookie.setMaxAge(0); // Set the max age to 0 to delete the cookie
        cookie.setPath("/"); // Set the path to match the original cookie
        response.addCookie(cookie);
    }

    public String generateToken() {
        String token = jwtUtil.generateToken(this.claim, this.expTime);
        String signedToken = signCookieValue(token);

        ResponseCookie cookie = ResponseCookie.from(this.cookieName, signedToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(this.expTime))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return token;
    }

    private String signCookieValue(String value) {
        String data = value + ":" + jwtCookieSecret;
        String signature = jwtUtil.generateSignature(data); // Assuming you have a method to generate a signature
        return value + ":" + signature;
    }

    public String verifyCookie(Cookie cookie) {
        if (cookie == null) {
            return null;
        }

        String cookieValue = cookie.getValue();
        if (cookieValue == null) {
            return null;
        }

        String[] parts = cookieValue.split(":");
        if (parts.length != 2) {
            return null;
        }

        String value = parts[0];
        String signature = parts[1];

        String data = value + ":" + jwtCookieSecret;
        String expectedSignature = jwtUtil.generateSignature(data); // Generate the expected signature

        if (signature.equals(expectedSignature)) {
            return value; // Cookie is valid
        } else {
            return null; // Cookie is invalid
        }
    }

}
