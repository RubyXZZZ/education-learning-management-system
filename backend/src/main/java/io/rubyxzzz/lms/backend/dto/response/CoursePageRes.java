package io.rubyxzzz.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePageRes {
    private String id;
    private String title;
    private String body;

    private String courseSectionId;

    // Module Info
    private String moduleId;
    private String moduleName;

    private Boolean isPublished;
    private Integer orderNum;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
