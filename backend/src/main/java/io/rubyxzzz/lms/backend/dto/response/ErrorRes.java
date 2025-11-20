package io.rubyxzzz.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard Error Response DTO
 * Returned for all API errors
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRes {
    private LocalDateTime timestamp;
    private int status;

    //Error type (e.g., "Not Found", "Bad Request")
    private String error;

    private String message;

    //API path where error occurred
    private String path;

    // Validation errors (field-level) Only present for validation failures
    private Map<String, String> validationErrors;
}
