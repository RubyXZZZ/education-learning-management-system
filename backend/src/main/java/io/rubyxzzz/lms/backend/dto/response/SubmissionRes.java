package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.SubmissionStatus;
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
public class SubmissionRes {
    private String id;

    // assignment info
    private String assignmentId;
    private String assignmentTitle;
    private Double totalPoints;
    private LocalDateTime dueDate;

    // student info
    private String studentId;
    private String studentNumber;
    private String studentName;
    private String studentEmail;

    // submission content
    private String content;  // For ONLINE_TEXT
    private String fileUrl;  // For ONLINE_FILE
    private String externalUrl;  // For ONLINE_URL

    // status
    private SubmissionStatus status;
    private Integer attemptNumber;
    private Boolean isLatest;

    // grading
    private Double grade;
    private String feedback;
    private LocalDateTime gradedAt;
    private String gradedBy;
    private String gradedByName;

    // timestamps
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
