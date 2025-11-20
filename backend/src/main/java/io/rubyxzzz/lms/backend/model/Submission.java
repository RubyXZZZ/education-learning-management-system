package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions", indexes = {
        @Index(name = "idx_assignment_id", columnList = "assignment_id"),
        @Index(name = "idx_student_id", columnList = "student_id"),
        @Index(name = "idx_assignment_student", columnList = "assignment_id, student_id"),
        @Index(name = "idx_assignment_latest", columnList = "assignment_id, is_latest"),
        @Index(name = "idx_student_latest", columnList = "student_id, is_latest"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_attempt_number", columnList = "attempt_number")
})

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Submission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "submission_type", length = 20)
//    private SubmissionType submissionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubmissionStatus status; // SUBMITTED, GRADED, LATE, UNSUBMITTED

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;  // For ONLINE_TEXT

    @Column(name = "file_url", length = 500)
    private String fileUrl;  // For ONLINE_FILE

    @Column(name = "external_url", length = 500)
    private String externalUrl;  // For ONLINE_URL

    @Column(name = "grade")
    private Double grade;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;

    @Column(name = "is_latest")
    private Boolean isLatest = true;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "graded_by", length = 36)
    private String gradedBy;

}

