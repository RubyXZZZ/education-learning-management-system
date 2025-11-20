package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCoursePageReq {
    @NotBlank(message = "Course section ID is required")
    private String courseSectionId;

    @NotBlank(message = "Module ID is required")
    private String moduleId;

    @NotBlank(message = "Page title is required")
    private String title;

    private String body;  // HTML content

    private Boolean isPublished = false;
    private Integer orderNum;
}
