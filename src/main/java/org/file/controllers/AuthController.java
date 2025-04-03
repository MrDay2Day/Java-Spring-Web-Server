package org.file.controllers;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.file.apiResponse.ApiResponse;
import org.file.database.DatabaseConnection;
import org.file.database.DatabaseDynamicQueryExecution;
import org.file.database.DatabaseType;
import org.file.database.models.User;
import org.file.utils.BcryptHashing;
import org.file.utils.GenerateCookie;
import org.file.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static JwtUtil jwtUtilWebSocket;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static JwtUtil jwtUtil;
    private static JwtUtil jwtUtilRefresh;


    private final DatabaseDynamicQueryExecution databasePrimaryQueryExecution;
    private final DatabaseDynamicQueryExecution databaseSecondaryQueryExecution;
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

    // Constructor-based Dependency Injection
    public AuthController(
            DatabaseConnection databaseConnection,
            @Value("${jwt.websocket.secret}") String jwtWebSocketSecret,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.refresh.secret}") String secretRefresh,
            @Value("${jwt.cookie.secret}") String cookieSecret
    ) {
        this.databasePrimaryQueryExecution  = new DatabaseDynamicQueryExecution(databaseConnection);
        this.databasePrimaryQueryExecution.selectDatabase(DatabaseType.PRIMARY);

        this.databaseSecondaryQueryExecution = new DatabaseDynamicQueryExecution(databaseConnection);
        this.databaseSecondaryQueryExecution.selectDatabase(DatabaseType.SECONDARY);

        jwtUtilWebSocket = new JwtUtil(jwtWebSocketSecret);
        jwtUtil = new JwtUtil(secret, cookieSecret);
        jwtUtilRefresh = new JwtUtil(secretRefresh, cookieSecret);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @RequestBody Map<String, String> requestBody,
            HttpServletResponse response) {
        try {
            // Validate request body
            if (requestBody == null || requestBody.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, null,
                                "Invalid Request", "Email and Password are required.",
                                1000021));
            }

            // Extract and validate fields
            String email = requestBody.get("email");
            String password = requestBody.get("password");

            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, null,
                                "Missing Credentials", "Email and Password are required.",
                                1000024));
            }

            // Execute SQL query with parameters to prevent SQL Injection
            // Fetch list of users
            List<User> users = databasePrimaryQueryExecution.executeSelectQuery(
                    "SELECT *, dob FROM users WHERE email = ?",
                    User::mapUser, // Pass User mapping method
                    email.trim().toLowerCase()
            );

            users.forEach(System.out::println);


            logger.info("Query Result: {}", users);

            if (users.size() != 1) {
                return ResponseEntity.status(401).body(
                        new ApiResponse<>(false,  "Invalid email or password.",
                                1000025));
            }

            User user = users.get(0);

            String savedPassword = users.get(0).getPassword();
            boolean verified = BcryptHashing.checkPassword(password, savedPassword);

            if (!verified) {
                return ResponseEntity.status(401).body(
                        new ApiResponse<>(false, "Invalid email or password.",
                                1000025));
            }

            // TODO: Add more validation for user, see below 👇🏿
            /*

              Do other verification checks

              Example:
                - Check if user is blocked
                - Fetch other information for the user

             */

            // Generates JWT Cookies Auth and Refresh
            getGenerateCookie(response, user);


            Map<String, Object> webSocketClaim = new HashMap<>();
            webSocketClaim.put("userId", String.valueOf(user.getId()));

            String webSocketToken = jwtUtilWebSocket.generateToken(webSocketClaim, 5);

            return ResponseEntity.ok(new ApiResponse<>(
                    true, user.toPublicInfo(webSocketToken),
                    "Successful Authentication "));

        } catch (Exception e) {
            logger.error("Error occurred during login process", e);
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(false,  "Something went wrong.",
                            1000023));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            HttpServletResponse response) {
        try {
            // Clear the cookies
            GenerateCookie.clearCookie(response, jwtCookieName);
            GenerateCookie.clearCookie(response, jwtRefreshCookieName);

            return ResponseEntity.ok(new ApiResponse<>(
                    true, "Logout successful."));
        } catch (Exception e) {
            logger.error("Error occurred during logout process", e);
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(false,  "Something went wrong.",
                            1000023));
        }
    }


    @GetMapping("/refresh-websocket-token")
    public ResponseEntity<ApiResponse<?>> refreshWebSocketToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        try {

            Claims claims = (Claims) request.getSession().getAttribute("claims");

            String userId = claims.get("userId", String.class);

            if(userId == null) {
                return ResponseEntity.status(401).body(
                        new ApiResponse<>(false, "Unauthorized!",
                                1000032));
            }

            Map<String, Object> webSocketClaim = new HashMap<>();
            webSocketClaim.put("userId", userId);

            String webSocketToken = jwtUtilWebSocket.generateToken(webSocketClaim, 5);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    webSocketToken,
                    "WebSocket token refreshed successfully."));
        } catch (Exception e) {
            logger.error("Error occurred during logout process", e);
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(false,  "Something went wrong.",
                            1000023));
        }
    }


    private void getGenerateCookie(HttpServletResponse response, User user) {
        Map<String, Object> authCliams = new HashMap<>();
        authCliams.put("userId", String.valueOf(user.getId()));
        authCliams.put("email", user.getEmail());
        authCliams.put("firstName", user.getFirstName());
        authCliams.put("lastName", user.getLastName());

        GenerateCookie generateJwtCookie = new GenerateCookie(
                jwtCookieName,
                jwtExpirationSeconds,
                authCliams,
                response,
                jwtUtil );
        String token = generateJwtCookie.generateToken();
        GenerateCookie generateRefreshJwtCookie = new GenerateCookie(
                jwtRefreshCookieName,
                jwtExpirationRefreshSeconds,
                authCliams,
                response,
                jwtUtilRefresh );
        String refreshToken = generateRefreshJwtCookie.generateToken();
    }
}
