package org.file.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Arrays;
import java.util.Enumeration;

import org.springframework.stereotype.Controller;

@Controller
public class CookieController {
    @GetMapping("/headers-cookies")
    public ResponseEntity<String> getHeadersAndCookies(HttpServletRequest request, @RequestHeader("My-Custom-Header") String customHeader) {

        // Accessing headers
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println("Header: " + headerName + " = " + headerValue);
        }

        // Accessing a specific header using @RequestHeader
        System.out.println("Custom Header: " + customHeader);

        // Accessing cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).forEach(cookie ->
                    System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue())
            );
        }

        return ResponseEntity.ok("Headers and cookies accessed");
    }
}
