package org.file.controllers;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.file.apiResponse.ApiResponse;
import org.file.utils.GenerateCookie;
import org.file.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class JwtController {

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


    public JwtController(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.refresh.secret}") String secretRefresh,
            @Value("${jwt.cookie.secret}") String cookieSecret
    ) {
        this.jwtUtil = new JwtUtil(secret, cookieSecret);
        this.jwtUtilRefresh = new JwtUtil(secretRefresh, cookieSecret);
    }

    @GetMapping("/generate-jwt")
    public ResponseEntity<ApiResponse<String>> generateJwt(HttpServletResponse response) {

        // User JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "day2day_boss");
        claims.put("role", "admin");

        /* GENERATE COOKIE AND JWT */

        // String token = jwtUtil.generateToken(claims, jwtExpirationMinutes);
        // Cookie jwtCookie = new Cookie(jwtCookieName, token);
        // jwtCookie.setHttpOnly(true); // Important: Prevents client-side JavaScript access
        // jwtCookie.setSecure(true); // Important: Only send over HTTPS
        // jwtCookie.setPath("/"); // Adjust the path as needed
        // jwtCookie.setMaxAge(jwtExpirationMinutes * 60); // Cookie expiration in seconds
        // response.addCookie(jwtCookie);

        // Refresh Token

        // claims.put("jwt", token);
        // String refreshToken = jwtUtilRefresh.generateToken(refreshClaims, jwtExpirationRefreshMinutes);
        // Cookie jwtResfreshCookie = new Cookie(jwtRefreshCookieName, refreshToken);
        // jwtResfreshCookie.setHttpOnly(true); // Important: Prevents client-side JavaScript access
        // jwtResfreshCookie.setSecure(true); // Important: Only send over HTTPS
        // jwtResfreshCookie.setPath("/"); // Adjust the path as needed
        // jwtResfreshCookie.setMaxAge(jwtExpirationRefreshMinutes * 60); // Cookie expiration in seconds
        // response.addCookie(jwtResfreshCookie);

        /* Custom Class to generate cookie and JWT */
        GenerateCookie generateJwtCookie = new GenerateCookie(
                jwtCookieName,
                jwtExpirationSeconds,
                claims,
                response,
                jwtUtil );
        String token = generateJwtCookie.generateToken();
        // claims.put("jwt", token);
        GenerateCookie generateRefreshJwtCookie = new GenerateCookie(
                jwtRefreshCookieName,
                jwtExpirationRefreshSeconds,
                claims,
                response,
                jwtUtilRefresh );
        String tokenRefresh = generateRefreshJwtCookie.generateToken();



        return ResponseEntity.ok(new ApiResponse<>(
                true,
                claims.toString(),
                "Token Created"));
    }

    @GetMapping("/test-jwt")
    public ResponseEntity<String> testJwt(jakarta.servlet.http.HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtCookieName)) {
                    String token = cookie.getValue();
                    if(jwtUtil.isTokenExpired(token)){
                        return ResponseEntity.badRequest().body("Token Expired");
                    }
                    String username = jwtUtil.extractClaim(token, "username");
                    String role = jwtUtil.extractClaim(token, "role");
                    return ResponseEntity.ok("Username: "+ username + " Role: " + role);
                }
            }
        }
        return ResponseEntity.badRequest().body("JWT Cookie not found.");
    }

    @GetMapping("/test-refresh")
    public ResponseEntity<ApiResponse<String>> testJwtRefresh(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            // Search for AUTH Token
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtCookieName)) {
                    String token = new GenerateCookie(jwtUtil).verifyCookie(cookie);

                    if (!jwtUtil.isTokenExpired(token)) {
                        String username = jwtUtil.extractClaim(token, "username");
                        String role = jwtUtil.extractClaim(token, "role");

                        Map<String, String> userData = new HashMap<>();
                        userData.put("username", username);
                        userData.put("role", role);

                        return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                userData.toString(), // Return the Map directly
                                "Token Valid!"));
                    }
                    break;
                }
            }

            // Search for Refresh Token to create new AUTH token
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtRefreshCookieName)) {
                    String tokenRefresh = new GenerateCookie(jwtUtilRefresh).verifyCookie(cookie);
                    if (tokenRefresh == null || jwtUtilRefresh.isTokenExpired(tokenRefresh)) {
                        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid Token(s)."));
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

                    System.err.println("\n\n\nnewToken: " + newToken);
                    String newTokenRefresh = generateRefreshJwtCookie.generateToken();
                    System.out.println("\n\nRefresh Successfully. \n\n" +
                            "AuthToken: " + newToken + "\n" +
                            "Refresh Token: " + newTokenRefresh);

                    String username = jwtUtil.extractClaim(newToken, "username");
                    String role = jwtUtil.extractClaim(newToken, "role");

                    Map<String, String> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("role", role);

                    return ResponseEntity.ok(new ApiResponse<>(
                            true,
                            userData.toString(), // Return the Map directly
                            "Token Refresh!"));

                }
            }
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "JWT Cookie not found."));
    }
}

