package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Grade a submission (instructor only)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeSubmReq {
    @NotBlank(message = "Assignment ID is required")
    private String assignmentId;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Grade is required")
    @PositiveOrZero(message = "Grade must be positive or zero")
    private Double grade;

    private String feedback;

}
