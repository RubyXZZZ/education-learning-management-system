package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstructorRes {

    // From BaseEntity
    private String id;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // From User
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
    private boolean emailVerified;
//    private boolean phoneVerified;

    // From Instructor
    private String employeeNumber;
    private String department;
    private String officeHours;
    private int teachingCounts;

    private List<SectionRes> sections;

}
