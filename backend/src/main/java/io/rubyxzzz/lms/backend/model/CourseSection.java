package io.rubyxzzz.lms.backend.model;


import io.rubyxzzz.lms.backend.constants.BusinessConstants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Section Entity
 * Represents a specific class section of a course in a particular session
 *
 * Example: ESL-301-LS-A (2025-S1, Teacher Li, Mon/Wed 10:00, Room 201)
 *
 * Relationships:
 * - Belongs to one Course (template)
 * - Belongs to one Session (time period)
 * - Has one Instructor
 * - Has many Enrollments (students)
 */
@Entity
@Table(name = "course_sections", indexes = {
        @Index(name = "idx_course_id", columnList = "course_id"),
        @Index(name = "idx_session_id", columnList = "session_id"),
        @Index(name = "idx_instructor_id", columnList = "instructor_id"),
        @Index(name = "idx_program_code", columnList = "program_code"),
        @Index(name = "idx_level_number", columnList = "level_number"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_session_code", columnList = "session_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CourseSection extends BaseEntity {

    // ===== Course Association =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_section_course")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course; // FK to Course

    @Column(name = "course_code", length = 50)
    private String courseCode;  // Denormalized from Course

    @Column(name = "course_name", length = 100)
    private String courseName;  // Denormalized from Course

    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;  // Denormalized from Course

    @Column(name = "section_code", length = 10)
    private String sectionCode;  // e.g., "A", "B", "C"

    // ===== Session Association =====


    @Column(name = "session_code", length = 20)
    private String sessionCode;  // Denormalized from Session


    // ===== Schedule (Section-specific) =====

    @Enumerated(EnumType.STRING)
    @Column(name = "course_format", length = 20)
    private CourseFormat courseFormat;

    @Column(name = "schedule", length = 100)
    private String schedule;  // "Mon, Wed 10:00-11:30"

    @Column(name = "days_of_week", length = 100)
    private String daysOfWeek;  // "Monday,Wednesday"

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "location", length = 100)
    private String location;  // "Room 201" or "Online"

    // ===== Instructor (Section-specific) =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "instructor_id",
            foreignKey = @ForeignKey(name = "fk_section_instructor")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Instructor instructor;  // FK to Instructor

    @Column(name = "instructor_name", length = 100)
    private String instructorName; // Denormalized

    @Column(name = "instructor_email", length = 100)
    private String instructorEmail; // Denormalized

    // ===== Capacity (Section-specific) =====

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "min_enrollment", nullable = false)
    private Integer minEnrollment;

    @Column(name = "enrolled_count", nullable = false)
    private Integer enrolledCount = 0;

//    @Column(name = "waitlist_count", nullable = false)
//    private Integer waitlistCount = 0;


    // ===== Status =====

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CourseSectionStatus status = CourseSectionStatus.DRAFT;

    @Column(name = "enrollment_locked", nullable = false)
    private Boolean enrollmentLocked = false;

    // ===== Statistics =====

    @Column(name = "average_grade")
    private Double averageGrade;

    @Column(name = "completion_rate")
    private Double completionRate;

    @OneToMany(
            mappedBy = "section",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Enrollment> enrollments = new ArrayList<>();

    // ===== Business Methods =====

    /**
     * Check if section has available seats
     */
    public boolean hasAvailableSeats() {
        return enrolledCount != null && capacity != null && enrolledCount < capacity;
    }

    /**
     * Get available seats
     */
    public Integer getAvailableSeats() {
        if (capacity == null || enrolledCount == null) {
            return 0;
        }
        return capacity - enrolledCount;
    }

    /**
     * Check if meets minimum enrollment
     */
    public boolean meetsMinimumEnrollment() {
        return enrolledCount != null && minEnrollment != null &&
                enrolledCount >= minEnrollment;
    }

    /**
     * Check if section is full
     */
    public boolean isFull() {
        return enrolledCount != null && capacity != null && enrolledCount >= capacity;
    }

    /**
     * Check if open for enrollment
     */
    public boolean isOpenForEnrollment() {
        if (status != CourseSectionStatus.PUBLISHED) {
            return false;
        }

        if (enrollmentLocked) {
            return false;
        }

        if (isFull()) {
            return false;
        }

        return true;
    }

    /**
     * Enroll a student
     */
    public void enrollStudent() {
        if (isFull()) {
            throw new IllegalStateException("Section is full");
        }

        if (enrolledCount == null) {
            enrolledCount = 0;
        }

        this.enrolledCount++;

    }

    /**
     * Drop a student
     */
    public void dropStudent() {
        if (enrolledCount != null && enrolledCount > 0) {
            this.enrolledCount--;

        }
    }
}