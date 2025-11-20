package io.rubyxzzz.lms.backend.dto.listItem;

import io.rubyxzzz.lms.backend.model.StudentType;
import io.rubyxzzz.lms.backend.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentList {
    private String id;
    private String studentNumber;
    private String fullName;
    private String email;
    private UserStatus status;
    private StudentType studentType;

    private Integer placementLevel;

    private Integer enrolledCounts;
    private Integer totalHoursEnrolled;

    private Double gpa;

}
