package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.StudentType;
import io.rubyxzzz.lms.backend.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Update Student Request
 * All fields are optional
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentReq {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String userAvatar;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private StudentType studentType;

    private Boolean emailVerified;
//    private Boolean phoneVerified;

    private String emergencyContact;
    private String emergencyPhone;


//    private Integer curLevelNumber;
    private Integer placementLevel;
    private LocalDate placementTestDate;


    private UserStatus status;
}
