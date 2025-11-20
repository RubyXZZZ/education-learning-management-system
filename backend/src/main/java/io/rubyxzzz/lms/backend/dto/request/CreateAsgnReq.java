package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.AssignmentType;
import io.rubyxzzz.lms.backend.model.SubmissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAsgnReq {

    @NotBlank(message = "Course section ID is required")
    private String courseSectionId;

//    private String moduleId;  // Optional

    @NotBlank(message = "Assignment title is required")
    private String title;

    private String content;  // HTML instructions

    @NotNull(message = "Assignment type is required")
    private AssignmentType assignmentType;

    private SubmissionType submissionType;

    private Double totalPoints;

    private LocalDateTime dueDate;

    private Integer maxAttempts;

    private Integer timeLimit;  // For quizzes

    private Boolean showCorrectAnswers = false;

    private Boolean isPublished = false;
}
