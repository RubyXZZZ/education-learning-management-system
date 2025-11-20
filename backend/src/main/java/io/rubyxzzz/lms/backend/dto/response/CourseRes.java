package io.rubyxzzz.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRes {
    // ===== Identity =====
    private String id;
    private String courseCode;
    private String courseName;
    private String courseDescription;
    private String sessionId;
    private String sessionCode;

    // ===== Prerequisites =====
    private Set<String> prerequisiteCourses;
    private Integer requiredPlacementLevel;
    private Boolean allowHigherPlacement;

    // ===== Course Properties =====
    private Integer hoursPerWeek;


    // === Status ===
    private Boolean isActive;
    private Integer sectionsCount;
    private List<SectionRes> sections;

    // === Timestamps ===
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}

