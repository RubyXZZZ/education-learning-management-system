package io.rubyxzzz.lms.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateModuleReq {
    private String name;
    private String description;
    private Integer orderNum;
    private Boolean isPublished;
}
