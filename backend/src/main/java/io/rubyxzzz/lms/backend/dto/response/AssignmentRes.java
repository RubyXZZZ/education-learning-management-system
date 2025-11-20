package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.AssignmentType;
import io.rubyxzzz.lms.backend.model.SubmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AssignmentRes {
    private String id;
    private String title;
    private String content;

    private String courseSectionId;
//    private String moduleId;

    private AssignmentType assignmentType;
    private SubmissionType submissionType;
    private Double totalPoints;
    private LocalDateTime dueDate;
    private Integer timeLimit;  // For quizzes (minutes)
    private Boolean showCorrectAnswers;  // For quizzes

    private Boolean isPublished;
    private Boolean isOverdue;  // Calculated
    private Boolean acceptsSubmissions;  // Calculated

    private Integer submissionCount;
    private Integer gradedCount;
    private Integer maxAttempts;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
