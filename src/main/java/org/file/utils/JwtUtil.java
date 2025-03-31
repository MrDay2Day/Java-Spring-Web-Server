package org.file.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {

    private final SecretKey secretKey;

    private String jwtCookieSecret;

    public JwtUtil(String secret) {
        // Ensure secret is sufficiently long
        if (secret.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits long");
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtUtil(String secret, String _jwtCookieSecret) {
        // Ensure secret is sufficiently long
        if (secret.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits long");
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtCookieSecret = _jwtCookieSecret;
    }


    public String generateToken(Map<String, Object> claims, int expirationSeconds) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() //Use Jwts.parserBuilder() instead of Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        System.err.println(claims);
        return claimsResolver.apply(claims);
    }

    public String extractClaim(String token, String claimName) {

        return extractClaim(token, claims -> claims.get(claimName, String.class));
    }

    public Date extractExpiration(String token) {
        Date expDate = extractClaim(token, Claims::getExpiration);
        System.err.println("ExpDate: " + expDate);
        return expDate;
    }

    public boolean isTokenExpired(String token) {
        System.err.println(token);
        boolean isExpired = extractExpiration(token).before(new Date());
        System.err.println("isExpired " + isExpired);
        return isExpired;
    }

    public String generateSignature(String data) {
        try {
            System.err.println("this.jwtCookieSecret: " + this.jwtCookieSecret);
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.jwtCookieSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating signature", e);
        }
    }
}