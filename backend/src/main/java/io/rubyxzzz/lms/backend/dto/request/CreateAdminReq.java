package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminReq {
    @NotBlank(message = "Employee number is required")
    private String employeeNumber;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String address;
    private LocalDate dateOfBirth;
    private String gender;

    // Admin specific
    private String department;
    private String position;
    private String officeHours;
    private Boolean isSuperAdmin; // Optional, defaults to false
//    private Set<String> deptScope;
}
