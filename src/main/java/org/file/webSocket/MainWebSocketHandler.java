package org.file.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private void cleanupConnections(String userId) {
        synchronized (userSessions) {
            ArrayList<WebSocketSession> webSocketSessions;
            webSocketSessions = userSessions.get(userId);

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
                            logger.error("Error closing WebSocket session for user {}: {}", userId, e.getMessage());
                        }

                        logger.info("Session removed for user: {}", userId);
                    }
                }
                if (webSocketSessions.isEmpty()) {
                    userSessions.remove(userId); // Remove the user entry if no sessions remain
                    logger.info("Session sessions removed for {}", userId);
                }else{
                    logger.info("Session count for user: {}, sessions: {}", userId,webSocketSessions.size() );
                }
                logger.info("Session cleanup for user: {}", userId);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = extractUserId(session);
        if (userId != null) {
            // Synchronize to prevent race conditions
            synchronized (userSessions) {
                // Remove any existing session for this user
                ArrayList<WebSocketSession> webSocketSessions = userSessions.get(userId);
                if(webSocketSessions != null && !webSocketSessions.isEmpty()){
                    webSocketSessions.add(session);
                    System.err.println("webSocketSessions = " + webSocketSessions.size());
                }else{
                    ArrayList<WebSocketSession> newSessions = new ArrayList<>();
                    newSessions.add(session);
                    userSessions.put(userId, newSessions);
                }
                cleanupConnections(userId); // Clean up any closed sessions

                logger.info("WebSocket connection established for user: {}", userId);
                logger.info("Current active sessions: {}", userSessions.keySet());
            }
        } else {
            logger.warn("WebSocket connection established without userId attribute.");
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = extractUserId(session);
        if (userId != null) {
            synchronized (userSessions) {
                cleanupConnections(userId); // Clean up any closed sessions
                logger.info("WebSocket connection closed for user: {}, status: {}", userId, status);
            }


        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = extractUserId(session);
        if (userId != null) {
            cleanupConnections(userId); // Clean up any closed sessions
            logger.info("Message received from user {}: {}", userId, message.getPayload());
            session.sendMessage(new TextMessage("Server received: " + message.getPayload()));
        }
    }

    // Publicly accessible method to get session
    public ArrayList<WebSocketSession> getSessionForUser(String userId) {
        return userSessions.get(userId);
    }

    // Method to send a message to a specific user with improved error handling
    public void sendMessageToUser(String userId, String message) throws IOException {
        cleanupConnections(userId); // Clean up any closed sessions
        ArrayList<WebSocketSession> userSession;
        synchronized (userSessions) {
            userSession = userSessions.get(userId);
            if (userSession == null || userSession.isEmpty()) {
                logger.warn("No active session found for user: {}", userId);
                throw new IOException("No active WebSocket session for user: " + userId);
            }else{
                for (WebSocketSession session : userSession) {
                    if (session != null && session.isOpen()) {
                        try {
                            logger.info("Sending message to user {}: {}", userId, message);
                            session.sendMessage(new TextMessage(message));
                        } catch (IOException e) {
                            logger.error("Error sending message to user {}: {}", userId, e.getMessage());
                            synchronized (userSessions) {
                                userSessions.remove(userId);
                            }
                            throw e;
                        }
                    } else {
                        logger.warn("No active session found for user: {}", userId);
                        synchronized (userSessions) {
                            userSessions.remove(userId);
                        }
                        throw new IOException("No active WebSocket session for user: " + userId);
                    }
                }
            }
        }
    }

    // Helper method to extract userId consistently
    private String extractUserId(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        cleanupConnections(userId); // Clean up any closed sessions
        return userId;

    }
}