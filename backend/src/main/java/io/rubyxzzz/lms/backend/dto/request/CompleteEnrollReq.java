package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CompleteEnrollReq {
    private String enrollmentId;

    @NotNull(message = "Final grade is required")
    @Min(value = 0, message = "Grade must be between 0 and 100")
    @Max(value = 100, message = "Grade must be between 0 and 100")
    private Double finalGrade;

    //private String letterGrade;
}
