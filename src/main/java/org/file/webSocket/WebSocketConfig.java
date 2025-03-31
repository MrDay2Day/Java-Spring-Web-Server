package org.file.webSocket;

import jakarta.servlet.http.HttpServletRequest;
import org.file.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final TokenValidator tokenValidator;

    public WebSocketConfig(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MainWebSocketHandler(), "/ws")
                .setAllowedOrigins("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        logger.info("WebSocket handshake attempt");

                        if (request instanceof ServletServerHttpRequest) {
                            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

                            String token = servletRequest.getParameter("token");
                            if (token == null) {
                                token = servletRequest.getHeader("Authorization");
                                if (token != null && token.startsWith("Bearer ")) {
                                    token = token.substring(7);
                                }
                            }

                            if (token == null) {
                                logger.warn("No token provided in WebSocket handshake");
                                return false;
                            }

                            String username = tokenValidator.getUsernameFromToken(token);
                            if (username == null) {
                                logger.warn("Invalid token!");
                                return false;
                            }

                            attributes.put("username", username);
                            logger.info("WebSocket handshake successful for user: {}", username);
                            return true;
                        }

                        logger.warn("Non-servlet request received during WebSocket handshake");
                        return false;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                        if (exception != null) {
                            logger.error("WebSocket handshake error", exception);
                        }
                    }
                });
    }

    public interface TokenValidator {
        boolean validateToken(String token);
        String getUsernameFromToken(String token);
    }

    @Component
    public static class TokenValidatorClass implements TokenValidator {

        private final String jwtWebSocketSecret;
        private static JwtUtil jwtUtilWebSocket;
        private static final Logger logger = LoggerFactory.getLogger(TokenValidatorClass.class);

        public TokenValidatorClass(@Value("${jwt.websocket.secret}") String jwtWebSocketSecret) {
            this.jwtWebSocketSecret = jwtWebSocketSecret;
            jwtUtilWebSocket = new JwtUtil(jwtWebSocketSecret);
        }

        @Override
        public boolean validateToken(String token) {
            boolean isValid = false;
            try {
                logger.error("WebSocket Token: {}", token);
                isValid = !jwtUtilWebSocket.isTokenExpired(token);
            } catch (Exception e) {
                logger.error("Token validation error: {}", e.getMessage());
            }
            logger.info("Token validation result: {} secret: {}", isValid, this.jwtWebSocketSecret);
            return isValid;
        }

        @Override
        public String getUsernameFromToken(String token) {
            if (token != null && !token.isEmpty()) {
                boolean isValid = validateToken(token);
                if (isValid) {
                    String userId = jwtUtilWebSocket.extractClaim(token, "userId");
                    if (userId != null && !userId.isEmpty()) {
                        return userId;
                    }
                }
                return null;
            }
            logger.warn("Failed to extract username from token");
            return null;
        }
    }
}