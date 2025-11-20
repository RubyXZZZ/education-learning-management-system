package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.UserRole;
import io.rubyxzzz.lms.backend.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes {

    // User Information
    private String userId;
    private String email;
    private String fullName;
    private String userNumber;  // studentNumber or employeeNumber
    private UserRole role;
    private UserStatus status;
    private String userAvatar;
    private Boolean isSuperAdmin;
}