package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseReq {
//    @NotBlank(message = "Program UUID is required")
//    private String programId;
    @NotBlank(message = "Session UUID is required")
    private String sessionId;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    private String courseDescription;


    private Set<String> prerequisiteCourses;    // Course codes
    private Integer requiredPlacementLevel;
    private Boolean allowHigherPlacement;


    @NotNull(message = "Hours per week is required")
    @Positive(message = "Hours must be positive")
    private Integer hoursPerWeek;


}
