package io.rubyxzzz.lms.backend.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// All fields optional
public class UpdateCourseReq {
    private String courseName;
    private String courseDescription;

    private List<String> prerequisiteCourses;
    private Integer requiredPlacementLevel;
    private Boolean allowHigherPlacement;

    private Integer hoursPerWeek;


    private Boolean isActive;
}
