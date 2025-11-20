package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Assignment Entity
 * Represents homework, quizzes, exams, discussions, and other graded activities
 *
 * The instructions field stores HTML that can contain:
 * - Formatted text
 * - Embedded images
 * - File links
 * - External links
 */

@Entity
@Table(name = "assignments", indexes = {
        @Index(name = "idx_course_section", columnList = "course_section_id"),
        @Index(name = "idx_module", columnList = "module_id"),
        @Index(name = "idx_due_date", columnList = "due_date"),
        @Index(name = "idx_published", columnList = "is_published"),
        @Index(name = "idx_section_published", columnList = "course_section_id, is_published"),
        @Index(name = "idx_assignment_type", columnList = "assignment_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Assignment extends BaseEntity {

    // The course section this assignment belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_section_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_assignment_section")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseSection courseSection;

    //Optional: module for content organization
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "module_id",
//            foreignKey = @ForeignKey(name = "fk_assignment_module")
//    )
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Module module;


    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false, length = 20)
    private AssignmentType assignmentType;


    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", length = 20)
    private SubmissionType submissionType;

    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;


    @Column(name = "total_points", nullable = false)
    private Double totalPoints;


    @Column(name = "due_date")
    private LocalDateTime dueDate;

    // for quizzes/exams
    @Column(name = "time_limit")
    private Integer timeLimit;

    //Whether to show correct answers after submission (for QUIZ type)
    @Column(name = "show_correct_answers")
    private Boolean showCorrectAnswers = false;


    @Column(name = "is_published")
    private Boolean isPublished = false;

    @OneToMany(
            mappedBy = "assignment",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Submission> submissions = new ArrayList<>();

    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate);
    }

    public boolean acceptsSubmissions() {
        return Boolean.TRUE.equals(isPublished)
                && !isOverdue()
                && submissionType != null
                && !SubmissionType.NO_SUBMISSION.equals(submissionType);
    }

//    public boolean isVisibleToStudent() {
//        if (!Boolean.TRUE.equals(isPublished)) return false;
//        if (module != null && !Boolean.TRUE.equals(module.getIsPublished())) return false;
//        return true;
//    }

}