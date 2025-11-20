package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.*;
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
public class StudentRes {
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
    private String nationality;

    private Boolean emailVerified;
//    private Boolean phoneVerified;

    private String emergencyContact;
    private String emergencyPhone;

    // From Student
    private String studentNumber;

    private StudentType studentType;
    private Integer maxHoursAllowed;       // Calculated
    private Integer minHoursRequired;      // Calculated


//    private Integer curLevelNumber;              // e.g., 3


    // ===== Placement Test =====
    private Integer placementLevel;
    private LocalDate placementTestDate;

    // ===== Enrollment Tracking =====
    private Integer enrolledCounts;              // Current active enrollments
    private Integer totalHoursEnrolled;          // Total hours in current session
    //private EnrollmentMode enrollmentMode;       // FULL_TIME or PART_TIME (calculated)
    private Boolean canEnrollMore;
    private Boolean canEnrollCourse;       // Calculated


    // ===== Academic History =====
//    private Double gpa;
    private Integer totalCoursesCompleted;
//    private Integer totalCoursesPassed;
//    private Integer totalCoursesFailed;
//    private Double passRate;                     // Calculated: passed/completed * 100

    private List<EnrollmentRes> enrollments;
}

