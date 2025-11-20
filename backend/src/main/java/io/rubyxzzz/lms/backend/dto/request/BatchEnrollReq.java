package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEnrollReq {

    @NotEmpty(message = "Student IDs cannot be empty")
    private List<String> studentId;

    @NotBlank(message = "Course section ID is required")
    private String courseSectionId;
}
