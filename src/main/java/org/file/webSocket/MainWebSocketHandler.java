package org.file.webSocket;

import org.file.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MainWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(MainWebSocketHandler.class);

    // Use a static map to ensure session persistence across handler instances
    private static final Map<String, ArrayList<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    private void cleanupConnections(String username) {
        synchronized (userSessions) {
            ArrayList<WebSocketSession> webSocketSessions;
            webSocketSessions = userSessions.get(username);

            if (webSocketSessions != null && !webSocketSessions.isEmpty()) {
                Iterator<WebSocketSession> iterator = webSocketSessions.iterator();
                while (iterator.hasNext()) {
                    WebSocketSession session = iterator.next();
                    if (session == null || !session.isOpen()) {
                        try {
                            if (session != null && !session.isOpen()) {
                                session.close(CloseStatus.NORMAL);
                                iterator.remove(); // Remove the closed session from the list
                            }else if(session == null){
                                iterator.remove(); // Remove the blank value from the list
                            }
                        } catch (IOException e) {
                            logger.error("Error closing WebSocket session for user {}: {}", username, e.getMessage());
                        }

                        logger.info("Session removed for user: {}", username);
                    }
                }
                if (webSocketSessions.isEmpty()) {
                    userSessions.remove(username); // Remove the user entry if no sessions remain
                    logger.info("Session sessions removed for {}", username);
                }else{
                    logger.info("Session count for user: {}, sessions: {}", username,webSocketSessions.size() );
                }
                logger.info("Session cleanup for user: {}", username);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = extractUsername(session);
        if (username != null) {
            // Synchronize to prevent race conditions
            synchronized (userSessions) {
                // Remove any existing session for this user
                ArrayList<WebSocketSession> webSocketSessions = userSessions.get(username);
                if(webSocketSessions != null && !webSocketSessions.isEmpty()){
                    webSocketSessions.add(session);
                    System.err.println("webSocketSessions = " + webSocketSessions.size());
                }else{
                    ArrayList<WebSocketSession> newSessions = new ArrayList<>();
                    newSessions.add(session);
                    userSessions.put(username, newSessions);
                }
            }

            cleanupConnections(username); // Clean up any closed sessions

            logger.info("WebSocket connection established for user: {}", username);
            logger.info("Current active sessions: {}", userSessions.keySet());
        } else {
            logger.warn("WebSocket connection established without username attribute.");
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = extractUsername(session);
        if (username != null) {
            synchronized (userSessions) {
                cleanupConnections(username); // Clean up any closed sessions
            }

            logger.info("WebSocket connection closed for user: {}, status: {}", username, status);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String username = extractUsername(session);
        if (username != null) {
            cleanupConnections(username); // Clean up any closed sessions
            logger.info("Message received from user {}: {}", username, message.getPayload());
            session.sendMessage(new TextMessage("Server received: " + message.getPayload()));
        }
    }

    // Publicly accessible method to get session
    public ArrayList<WebSocketSession> getSessionForUser(String username) {
        return userSessions.get(username);
    }

    // Method to send a message to a specific user with improved error handling
    public void sendMessageToUser(String username, String message) throws IOException {
        cleanupConnections(username); // Clean up any closed sessions
        ArrayList<WebSocketSession> userSession;
        synchronized (userSessions) {
            userSession = userSessions.get(username);
        }

        if (userSession == null || userSession.isEmpty()) {
            logger.warn("No active session found for user: {}", username);
            throw new IOException("No active WebSocket session for user: " + username);
        }else{
            for (WebSocketSession session : userSession) {
                if (session != null && session.isOpen()) {
                    try {
                        logger.info("Sending message to user {}: {}", username, message);
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        logger.error("Error sending message to user {}: {}", username, e.getMessage());
                        synchronized (userSessions) {
                            userSessions.remove(username);
                        }
                        throw e;
                    }
                } else {
                    logger.warn("No active session found for user: {}", username);
                    synchronized (userSessions) {
                        userSessions.remove(username);
                    }
                    throw new IOException("No active WebSocket session for user: " + username);
                }
            }

        }

    }

    // Helper method to extract username consistently
    private String extractUsername(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        cleanupConnections(username); // Clean up any closed sessions
        return username;

    }
}