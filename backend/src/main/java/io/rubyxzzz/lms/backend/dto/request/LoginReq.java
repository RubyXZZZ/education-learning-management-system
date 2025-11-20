package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Request DTO
 * Contains ID token from frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {

    @NotBlank(message = "Login token is required")
    private String loginToken;
}