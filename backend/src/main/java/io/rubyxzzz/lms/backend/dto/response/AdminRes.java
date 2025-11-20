package io.rubyxzzz.lms.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.rubyxzzz.lms.backend.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRes {

    // From BaseEntity
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // From User (no password!)
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private Integer age;
    private String gender;
    private String userAvatar;
    private UserStatus status;
    private Boolean emailVerified;
//    private Boolean phoneVerified;

    // From Admin
    private String employeeNumber;
    private String department;
    private String position;
    private String officeHours;

    @JsonProperty("isSuperAdmin")
    private Boolean isSuperAdmin;
//    private Set<String> deptScope;

}
