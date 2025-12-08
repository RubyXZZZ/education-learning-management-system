package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Course Entity
 * Represents a language course offered by the school
 * Relationships:
 * - One Instructor teaches Course (1:N)
 * - Many Students enroll in Course (N:M via Enrollment)
 * - Course has many Modules (1:N)
 * - Course has many Assignments (1:N)
 */

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_course_session", columnList = "course_code,session_id", unique = true),
        @Index(name = "idx_program_id", columnList = "program_id"),
        @Index(name = "idx_program_code", columnList = "program_code"),
        @Index(name = "idx_level_number", columnList = "level_number"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "session_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_course_session")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Session session;

    @Column(name = "session_code", length = 20)
    private String sessionCode;

    // ===== Basic Info =====

    @Column(name = "course_code", unique = true, nullable = false, length = 50)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @Column(name = "course_description", columnDefinition = "TEXT")
    private String courseDescription;


    // ===== Prerequisites =====

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "course_prerequisites",
            joinColumns = @JoinColumn(name = "course_id")
    )
    @Column(name = "prerequisite_course_code")
    private Set<String> prerequisiteCourses = new HashSet<>();

    @Column(name = "required_placement_level")
    private Integer requiredPlacementLevel;

    @Column(name = "allow_higher_placement")
    private Boolean allowHigherPlacement;

    // ===== Course Requirements =====


    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;



    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * One Course has Many Sections
     */
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CourseSection> sections = new ArrayList<>();



    /**
     * Check if course has any prerequisites
     */
    public boolean hasPrerequisites() {
        return (prerequisiteCourses != null && !prerequisiteCourses.isEmpty()) ||
                requiredPlacementLevel != null;
    }

    /**
     * Check if student meets prerequisites
     * Can be satisfied by EITHER:
     * 1. Completed all prerequisite courses, OR
     * 2. Placement level meets requirement
     */
    public boolean meetsPrerequisites(
            List<String> completedCourses,
            Integer studentPlacementLevel) {

        if (!hasPrerequisites()) {
            return true;
        }

        // Option 1: Completed prerequisite courses
        if (meetsPrerequisiteCourses(completedCourses)) {
            return true;
        }

        // Option 2: Placement level meets requirement
        if (meetsPlacementRequirement(studentPlacementLevel)) {
            return true;
        }

        return false;
    }

    /**
     * Check if student has completed prerequisite courses
     */
    public boolean meetsPrerequisiteCourses(List<String> completedCourses) {
        if (prerequisiteCourses == null || prerequisiteCourses.isEmpty()) {
            return true;
        }

        return completedCourses != null &&
                completedCourses.containsAll(prerequisiteCourses);
    }

    /**
     * Check if placement level meets requirement
     */
    private boolean meetsPlacementRequirement(Integer studentPlacementLevel) {
        if (requiredPlacementLevel == null) {
            return true;  // No placement requirement
        }

        if (studentPlacementLevel == null) {
            return false;  // Student hasn't taken placement test
        }

        if (Boolean.TRUE.equals(allowHigherPlacement)) {
            // Allow higher placement: ≥
            return studentPlacementLevel >= requiredPlacementLevel;
        } else {
            // Exact match required: =
            return studentPlacementLevel.equals(requiredPlacementLevel);
        }
    }



    /**
     * add Section to Course
     */
    public void addSection(CourseSection section) {
        sections.add(section);
        section.setCourse(this);
    }

    /**
     * remove Section from Course
     */

    public void removeSection(CourseSection section) {
        sections.remove(section);
        section.setCourse(null);
    }

}