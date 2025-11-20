package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.StudentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentReq {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String address;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String gender;
    private String nationality;

    @NotNull(message = "Student type is required")
    private StudentType studentType;


    private Integer placementLevel;      // Optional: can be set after test
    private LocalDate placementTestDate; // Optional: date of placement test

    private String emergencyContact;
    private String emergencyPhone;


}
