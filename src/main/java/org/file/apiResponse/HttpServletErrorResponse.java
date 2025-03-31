package org.file.apiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class HttpServletErrorResponse {
    public static void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status.value());
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse<Object> apiResponse = new ApiResponse<>(false, null, message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
