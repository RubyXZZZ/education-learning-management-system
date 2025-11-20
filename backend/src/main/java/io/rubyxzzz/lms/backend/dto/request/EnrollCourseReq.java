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
public class EnrollCourseReq {
    @NotBlank(message = "Student UUID is required")
    private String studentId;

    @NotBlank(message = "Course section UUID is required")
    private String courseSectionId;

}
