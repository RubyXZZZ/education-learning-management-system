package io.rubyxzzz.lms.backend.model;

import io.rubyxzzz.lms.backend.constants.BusinessConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Enrollment Entity
 * Represents the relationship between Student and Course
 *
 * Business Rules:
 * - One student can enroll in many courses
 * - One course can have many students
 * - Enrollment has its own lifecycle (enrolled, dropped, completed)
 */
@Entity
@Table(name = "enrollments", indexes = {
        @Index(name = "idx_student_id", columnList = "student_id"),
        @Index(name = "idx_section_id", columnList = "section_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_student_status", columnList = "student_id, status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Enrollment extends BaseEntity {

    // ===== Student Association =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_enrollment_student")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    @Column(name = "student_number", length = 20)
    private String studentNumber;

    @Column(name = "student_name", length = 100)
    private String studentName;

    @Column(name = "student_email", length = 100)
    private String studentEmail;

    // ===== Course Section Association =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "section_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_enrollment_section")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseSection section;

    @Column(name = "section_code", length = 10)
    private String sectionCode;

    @Column(name = "course_code", length = 50)
    private String courseCode;


    @Column(name = "session_code", length = 20)
    private String sessionCode;


    // ===== Enrollment Info =====

//    @Enumerated(EnumType.STRING)
//    @Column(name = "enrollment_mode", length = 20)
//    private EnrollmentMode enrollmentMode;

    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;

    @Column(name = "enrolled_time")
    private LocalDateTime enrolledTime;


    @Column(name = "dropped_time")
    private LocalDateTime droppedTime;

    @Column(name = "dropped_by", length = 36)
    private String droppedBy;

    @Column(name = "drop_reason", columnDefinition = "TEXT")
    private String dropReason;

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    // ===== Academic record =====

    @Column(name = "final_grade")
    private Double finalGrade;

//    @Column(name = "letter_grade", length = 5)
//    private String letterGrade;


    // ===== Attendance =====

//    @Column(name = "attendance_rate", nullable = false)
//    private Double attendanceRate = 0.0;
//
//    @Column(name = "total_classes", nullable = false)
//    private Integer totalClasses = 0;
//
//    @Column(name = "attended_classes", nullable = false)
//    private Integer attendedClasses = 0;
//
//    @Column(name = "absent_classes", nullable = false)
//    private Integer absentClasses = 0;
//
//    @Column(name = "late_classes", nullable = false)
//    private Integer lateClasses = 0;




    /**
     * Check if enrollment is active
     */
    public boolean isActive() {
        return this.status == EnrollmentStatus.ENROLLED;
    }

    /**
     * Check if enrollment is completed
     */
    public boolean isCompleted() {
        return this.status == EnrollmentStatus.COMPLETED;
    }

    /**
     * Check if enrollment is dropped
     */
    public boolean isDropped() {
        return this.status == EnrollmentStatus.DROPPED;
    }




    /**
     * Complete the enrollment with final grade
     */
    public void complete(Double finalGrade) {
        this.status = EnrollmentStatus.COMPLETED;
        this.completedTime = LocalDateTime.now();
        this.finalGrade = finalGrade;
    }




    /**
     * Drop the enrollment
     * Can be dropped by student or admin
     */
    public void drop(String reason, String droppedByUserId) {
        if (!canDrop()) {
            throw new IllegalStateException(
                    "Cannot drop enrollment with status: " + this.status
            );
        }

        this.status = EnrollmentStatus.DROPPED;
        this.droppedTime = LocalDateTime.now();
        this.dropReason = reason;
        this.droppedBy = droppedByUserId;
    }

    /**
     * Check if enrollment can be dropped
     * Can only drop ENROLLED status
     */
    public boolean canDrop() {
        return this.status == EnrollmentStatus.ENROLLED;
    }

}