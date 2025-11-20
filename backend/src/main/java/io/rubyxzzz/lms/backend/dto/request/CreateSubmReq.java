package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.SubmissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubmReq {
    @NotBlank(message = "Assignment ID is required")
    private String assignmentId;


    private String content;  // For ONLINE_TEXT
    private String fileUrl;  // For ONLINE_FILE
    private String externalUrl;  // For ONLINE_URL



}
