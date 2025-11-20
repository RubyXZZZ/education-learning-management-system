package io.rubyxzzz.lms.backend.dto.request;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstructorReq {
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

    private String gender;

    // Instructor specific
    private String department;       // "English", "Spanish", "Chinese", etc.
    private String officeHours;      // "Mon/Wed 2-4 PM"
}
