package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.EnrollmentMode;
import io.rubyxzzz.lms.backend.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRes {
    // ===== Identity =====
    private String id;

    // ===== Student Info =====
    private String studentId;
    private String studentNumber;
    private String studentName;
    private String studentEmail;

    // ===== Course Section Info =====
    private String courseSectionId;
    private String sectionCode;
    private String courseId;
    private String courseCode;           // "ESL-LS-L1"
    private String courseName;           // "Listening & Speaking"



    // ===== Session Info =====
//    private String sessionId;
    private String sessionCode;          // "2025-S1"

    // ===== Enrollment Details =====
//    private EnrollmentMode enrollmentMode;  // FULL_TIME, PART_TIME
    private Integer hoursPerWeek;

    // ===== Timeline =====
    private LocalDateTime enrolledTime;
    private String createdBy;           // Who created this enrollment
    private LocalDateTime droppedTime;
    private String droppedBy;            // Who dropped this enrollment

    // ===== Status =====
    private EnrollmentStatus status;

    // ===== Academic Progress =====
    private Double finalGrade;           // 0-100
    //private String letterGrade;          // A, B, C, D, F

    // ===== Attendance =====
    //private Double attendanceRate;       // 0-100%
    //private Integer totalClasses;
    //private Integer attendedClasses;
    //private Integer absentClasses;
    //private Integer lateClasses;

    // === Instructor Info ===
    private String instructorId;
    private String instructorName;
    private String instructorEmail;

    // ===== Drop Info =====
    private String dropReason;

    // ===== Timestamps =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}