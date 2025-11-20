package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DropCourseReq {
    @NotBlank(message = "Enrollment UUID is required")
    private String enrollmentId;

    private String dropReason;  // Optional: reason for dropping

}
