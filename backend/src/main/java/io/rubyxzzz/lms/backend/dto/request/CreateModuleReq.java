package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateModuleReq {
    @NotBlank(message = "Course section ID is required")
    private String courseSectionId;

    @NotBlank(message = "Module name is required")
    private String name;

    private String description;

    @NotNull(message = "Order number is required")
    private Integer orderNum;

    private Boolean isPublished = false;

}
