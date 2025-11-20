package io.rubyxzzz.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRes {
    private String id;
    private String name;
    private String description;

    // section info
    private String courseSectionId;

    private Integer orderNum;
    private Boolean isPublished;

    // timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
