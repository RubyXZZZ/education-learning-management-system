package io.rubyxzzz.lms.backend.model;

import io.rubyxzzz.lms.backend.constants.BusinessConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_number", columnList = "student_number", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_student_type", columnList = "student_type"),
        @Index(name = "idx_cur_level_number", columnList = "cur_level_number"),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends User {

    @Column(name = "student_number", unique = true, nullable = false, length = 20)
    private String studentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_type", nullable = false, length = 20)
    private StudentType studentType;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "placement_level")
    private Integer placementLevel;

    @Column(name = "placement_test_date")
    private LocalDate placementTestDate;

    // ===== Enrollment Tracking =====

    @Column(name = "enrolled_counts", nullable = false)
    private Integer enrolledCounts = 0;

    @Column(name = "total_hours_enrolled", nullable = false)
    private Integer totalHoursEnrolled = 0;



    // ===== Academic History =====

//    @Column(name = "gpa")
//    private Double gpa;
//
    @Column(name = "total_courses_completed", nullable = false)
    private Integer totalCoursesCompleted = 0;
//
//    @Column(name = "total_courses_passed", nullable = false)
//    private Integer totalCoursesPassed = 0;
//
//    @Column(name = "total_courses_failed", nullable = false)
//    private Integer totalCoursesFailed = 0;

    @OneToMany(
            mappedBy = "student",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Enrollment> enrollments = new ArrayList<>();


    @Override
    public String getUserNumber() {
        return this.studentNumber;
    }

    @Override
    public UserRole getUserRole() {
        return UserRole.STUDENT;
    }

    @Override
    public boolean canEnrollCourse() {
        return this.isActive() && this.getEmailVerified();
    }

    @Override
    public boolean canTeachCourse() {
        return false;
    }


    /**
     * Check if student is FULL_TIME_ONLY (F-1, J-1, M-1)
     */
    public boolean isFullTimeOnlyStudent() {
        return studentType == StudentType.FULL_TIME_ONLY;
    }

    /**
     * Check if student is PART_TIME_ONLY (Tourist visa)
     */
    public boolean isPartTimeOnlyStudent() {
        return studentType == StudentType.PART_TIME_ONLY;
    }

    /**
     * Check if student is FLEXIBLE (US citizen/PR)
     */
    public boolean isFlexibleStudent() {
        return studentType == StudentType.FLEXIBLE;
    }

    /**
     * Get maximum hours allowed per week
     */
    public Integer getMaxHoursAllowed() {
        if (isPartTimeOnlyStudent()) {
            return BusinessConstants.FULLTIME_MIN_HOURS - 1;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Get minimum hours required per week
     */
    public Integer getMinHoursRequired() {
        if (isFullTimeOnlyStudent()) {
            return BusinessConstants.FULLTIME_MIN_HOURS;  // 18
        }
        return 0;
    }

//    /**
//     * Get current enrollment mode (calculated)
//     */
//    public EnrollmentMode getEnrollmentMode() {
//        if (totalHoursEnrolled == null ||
//                totalHoursEnrolled < BusinessConstants.FULLTIME_MIN_HOURS) {
//            return EnrollmentMode.PART_TIME;
//        }
//        return EnrollmentMode.FULL_TIME;
//    }



    /**
     * Check if student can enroll in more courses
     */
    public boolean canEnrollMore() {
        if (enrolledCounts == null) {
            enrolledCounts = 0;
        }
        return enrolledCounts < BusinessConstants.MAX_COURSE_ENROLLMENT;
    }

    /**
     * Enroll in a course (increase counts and hours)
     */
    public void enrollInCourse(Integer courseHours) {
        if (enrolledCounts == null) {
            enrolledCounts = 0;
        }
        if (totalHoursEnrolled == null) {
            totalHoursEnrolled = 0;
        }

        this.enrolledCounts++;
        this.totalHoursEnrolled += courseHours;
    }

    /**
     * Drop a course (decrease counts and hours)
     */
    public void dropCourse(Integer courseHours) {
        if (enrolledCounts != null && enrolledCounts > 0) {
            this.enrolledCounts--;
        }

        if (totalHoursEnrolled != null && totalHoursEnrolled >= courseHours) {
            this.totalHoursEnrolled -= courseHours;
        }
    }






    /**
     * Update academic statistics after course completion
     */
    public void updateAcademicRecord(Double finalGrade) {
        if (totalCoursesCompleted == null) {
            totalCoursesCompleted = 0;
        }
//        if (totalCoursesPassed == null) {
//            totalCoursesPassed = 0;
//        }
//        if (totalCoursesFailed == null) {
//            totalCoursesFailed = 0;
//        }

        this.totalCoursesCompleted++;


        // Recalculate GPA
//        if (gpa == null) {
//            this.gpa = finalGrade;
//        } else {
//            this.gpa = (gpa * (totalCoursesCompleted - 1) + finalGrade) / totalCoursesCompleted;
//        }
    }
}