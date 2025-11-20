package io.rubyxzzz.lms.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCoursePageReq {
    private String title;
    private String body;
    private String moduleId;  // Can move page to different module
    private Boolean isPublished;
    private Integer orderNum;
}
