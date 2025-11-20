package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.CourseFormat;
import io.rubyxzzz.lms.backend.model.CourseSectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionRes {
    // ===== Section Identity =====
    private String id;
    private String sectionCode;

    // ===== Course Template Info =====
    private String courseId;
    private String courseCode;
    private String courseName;
    private Integer hoursPerWeek;
    private String courseDescription;

    // ===== Prerequisites =====
    private Set<String> prerequisiteCourses;
    private Integer requiredPlacementLevel;
    private Boolean allowHigherPlacement;

    // ===== Session Info =====
    private String sessionId;
    private String sessionCode;            // "2025-S1"

    // ===== Schedule =====
    private CourseFormat courseFormat;
    private String schedule;
    private String daysOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;

    // ===== Instructor =====
    private String instructorId;
    private String instructorNumber;
    private String instructorName;
    private String instructorEmail;

    // ===== Enrollment =====
    private Integer capacity;
    private Integer minEnrollment;
    private Integer enrolledCount;
//    private Integer waitlistCount;
    private Integer availableSeats;        // Calculated
    private Double capacityUtilization;    // Calculated


    // ===== Status =====
    private CourseSectionStatus status;
    private Boolean enrollmentLocked;
    private Boolean openForEnrollment;     // Calculated
    private Boolean meetsMinimumEnrollment; // Calculated


    // ===== Statistics =====
    private Double averageGrade;
    private Double completionRate;

    // ===== Timestamps =====
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
