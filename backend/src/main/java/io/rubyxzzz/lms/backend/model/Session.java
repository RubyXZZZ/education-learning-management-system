package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Session Entity - like "terms" or "semesters"
 * Represents a time period during which courses are offered
 * 6 sessions per year
 *
 * Example: 2025-S1 (Jan 6 - Feb 28, 8 weeks)
 */
@Entity
@Table(name = "sessions", indexes = {
        @Index(name = "idx_session_code", columnList = "session_code", unique = true),
        @Index(name = "idx_year_number", columnList = "year, session_number", unique = true),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Session extends BaseEntity {

    @Column(name = "session_code", unique = true, nullable = false)
    private String sessionCode;  // e.g., "2025S1"



    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    // ===== Important Dates =====

    @Column(name = "registration_open_date")
    private LocalDate registrationOpenDate;
//
//    @Column(name = "registration_deadline")
//    private LocalDate registrationDeadline;


    @Column(name = "add_drop_deadline")
    private LocalDate addDropDeadline;
//
//    @Column(name = "withdraw_deadline")
//    private LocalDate withdrawDeadline;

    // ===== Statistics =====

    @Column(name = "total_courses_offered")
    private int totalCoursesOffered = 0;

    @Column(name = "total_enrollments")
    private int totalEnrollments = 0;

    @OneToMany(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Course> courses = new ArrayList<>();





    /**
     * Check if session is currently active
     */
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return today.isAfter(startDate) && today.isBefore(endDate);
    }

    /**
     * Get session status by calculating based on current date
     */
    @Transient
    public SessionStatus getStatus() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) return SessionStatus.UPCOMING;
        if (today.isAfter(endDate)) return SessionStatus.COMPLETED;
        return SessionStatus.ACTIVE;
    }

//    /**
//     * Check if registration is open
//     */
//    public boolean isRegistrationOpen() {
//        LocalDate today = LocalDate.now();
//        return today.isAfter(registrationOpenDate) && today.isBefore(registrationDeadline);
//    }
//
//    /**
//     * Check if students can add/drop courses
//     */
//    public boolean canAddDrop() {
//        LocalDate today = LocalDate.now();
//        return today.isBefore(addDropDeadline);
//    }
//
//    /**
//     * Check if students can withdraw from courses
//     */
//    public boolean canWithdraw() {
//        LocalDate today = LocalDate.now();
//        return today.isBefore(withdrawDeadline);
//    }
//
//    /**
//     * Check if session has started
//     */
//    public boolean hasStarted() {
//        return LocalDate.now().isAfter(startDate);
//    }
//
//    /**
//     * Check if session has ended
//     */
//    public boolean hasEnded() {
//        return LocalDate.now().isAfter(endDate);
//    }
}