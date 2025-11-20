package io.rubyxzzz.lms.backend.dto.listItem;

import io.rubyxzzz.lms.backend.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorList {

    private String id;
    private String employeeNumber;
    private String fullName;
    private String email;
    private String department;
    private int teachingCounts;
    private UserStatus status;

}
