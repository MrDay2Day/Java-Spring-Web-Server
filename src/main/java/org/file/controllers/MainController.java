package org.file.controllers;

import org.file.apiResponse.ApiResponse;
import org.file.middleware.Interceptors.MainInterceptor;
import org.file.webSocket.MainWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/secure")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final MainWebSocketHandler mainWebSocketHandler;

    public MainController(MainWebSocketHandler mainWebSocketHandler) {
        this.mainWebSocketHandler = mainWebSocketHandler;
    }



    @GetMapping("/get")
    public ResponseEntity<ApiResponse<String>> getRequest() {
        return ResponseEntity.ok(new ApiResponse<>(true, null,"GET request successful!"));
    }

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<String>> postRequest(@RequestBody String requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Request body is empty"));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "POST request received: " + requestBody));
    }

    @GetMapping("/params")
    public ResponseEntity<ApiResponse<String>> paramsRequest(@RequestParam("param1") String param1, @RequestParam("param2") int param2) {
        String result = "Params received: param1 = " + param1 + ", param2 = " + param2;
        return ResponseEntity.ok(new ApiResponse<>(true, result));
    }

    @GetMapping("/query")
    public ResponseEntity<ApiResponse<Map<String, String>>> queryRequest(@RequestParam Map<String, String> queryParams) {
        return ResponseEntity.ok(new ApiResponse<>(true, queryParams));
    }

    @GetMapping("/embedded/resource")
    public ResponseEntity<ApiResponse<String>> embeddedRoute() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Embedded route accessed!"));
    }

    @GetMapping("/here/{dynamic}")
    public ResponseEntity<ApiResponse<String>> dynamicGetRoute(@PathVariable("dynamic") String dynamic) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Dynamic GET route: " + dynamic));
    }

    @PostMapping("/here/{dynamic}")
    public ResponseEntity<ApiResponse<String>> dynamicPostRoute(@PathVariable("dynamic") String dynamic, @RequestBody String requestBody) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Dynamic POST route: " + dynamic + ", Body: " + requestBody));
    }


    @PostMapping("/send-websocket-message")
    public ResponseEntity<Map<String, String>> sendWebSocketMessage(
            @RequestBody Map<String, String> requestBody
    ) {
        String userId = requestBody.get("userId");
        String message = requestBody.get("message");

        if (userId == null || message == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "UserId and message are required."));
        }

        logger.info("API request received: username={}, message={}", userId, message);

        try {
            mainWebSocketHandler.sendMessageToUser(userId, message);
            return ResponseEntity.ok(Map.of("message", "Message sent to User ID: " + userId));
        } catch (IOException e) {
            logger.error("Failed to send WebSocket message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send message: " + e.getMessage()));
        }
    }

}