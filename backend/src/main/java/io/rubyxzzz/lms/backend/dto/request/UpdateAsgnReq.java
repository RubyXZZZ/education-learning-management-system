package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.AssignmentType;
import io.rubyxzzz.lms.backend.model.SubmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAsgnReq {
    private String title;
    private String content;
//    private String moduleId;
    private AssignmentType assignmentType;
    private SubmissionType submissionType;
    private Double totalPoints;
    private LocalDateTime dueDate;
    private Integer maxAttempts;
    private Integer timeLimit;
    private Boolean showCorrectAnswers;
    private Boolean isPublished;

}
