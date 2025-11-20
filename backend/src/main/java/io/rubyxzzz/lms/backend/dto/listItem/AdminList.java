package io.rubyxzzz.lms.backend.dto.listItem;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.rubyxzzz.lms.backend.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminList {
    private String id;
    private String employeeNumber;
    private String fullName;
    private String email;
    private String department;
    private String position;
    private int managedCounts;
    @JsonProperty("isSuperAdmin")
    private boolean isSuperAdmin;
    private UserStatus status;

}
