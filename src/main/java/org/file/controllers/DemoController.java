package org.file.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.file.apiResponse.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @param date     Kept as java.util.Date
 * @param dateTime Kept as java.util.Date
 *
 * @class DemoResponse
 * Replaces the Class DemoResponseModel
 * class DemoResponse {
 *     private final Integer number;
 *     private final String text;
 *     private final Boolean status;
 *     private final Double value;
 *     private final Date date;      // Kept as java.util.Date
 *     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC") // Always show the UTC time regardless of the server's location
 *     private final Date dateTime;// Kept as java.util.Date
 *
 *     public DemoResponse(Integer number, String text, Boolean status, Double value, Date date, Date dateTime) {
 *         this.number = number;
 *         this.text = text;
 *         this.status = status;
 *         this.value = value;
 *         this.date = date;
 *         this.dateTime = dateTime;
 *     }
 *
 *     // Getters and Setters
 *     public Integer getNumber() {
 *         return number;
 *     }
 *
 *     public String getText() {
 *         return text;
 *     }
 *
 *     public Boolean getStatus() {
 *         return status;
 *     }
 *
 *     public Double getValue() {
 *         return value;
 *     }
 *
 *     public Date getDate() {
 *         return date;
 *     }
 *
 *     public Date getDateTime() {
 *         return dateTime;
 *     }
 * }
 */
record DemoResponse(Integer number, String text, Boolean status, Double value, Date date,
                    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC") Date dateTime) {
    DemoResponse(Integer number, String text, Boolean status, Double value, Date date, Date dateTime) {
        this.number = number;
        this.text = text;
        this.status = status;
        this.value = value;
        this.date = date;
        this.dateTime = dateTime;
    }

    @Override
    public Date dateTime() {
        return dateTime;
    }
}

@RestController
@RequestMapping("/demo")
public class DemoController {
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse> pingRequest() {
        try {
            logger.info("Demo route accessed");

            // Parsing the string to LocalDate first (cleaner)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse("25-12-2023", formatter);

            // Convert LocalDate -> java.util.Date
            Date legacyDate = Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Convert ZonedDateTime (Jamaica) -> java.util.Date (UTC)
            Date legacyDateTime = Date.from(
                    LocalDateTime.of(1992, 5, 18, 8, 45)
                            .atZone(ZoneId.of("America/Jamaica"))
                            .toInstant()
            );

            return ResponseEntity.ok(
                    new ApiResponse(
                            true,
                            new DemoResponse(
                                    2390,
                                    "Hello World",
                                    true,
                                    1234567.89,
                                    legacyDate,
                                    legacyDateTime
                            ),
                            "Demo route is operational!"
                    )
            );
        } catch (Exception e) {
            logger.error("Error in demo route: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(
                            new ApiResponse<>(
                                    false,
                                    "Internal server error in demo route",
                                    1000045
                            )
                    );
        }


    }
}