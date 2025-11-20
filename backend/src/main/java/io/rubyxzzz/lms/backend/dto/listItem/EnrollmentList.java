package io.rubyxzzz.lms.backend.dto.listItem;

import io.rubyxzzz.lms.backend.model.EnrollmentMode;
import io.rubyxzzz.lms.backend.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EnrollmentList {
    private String id;
    private String studentNumber;
    private String studentName;
    private String studentEmail;
    private String sectionCode;
    private String courseCode;
    private String courseName;
    private String sessionCode;
//    private EnrollmentMode enrollmentMode;
    private Integer hoursPerWeek;
    private LocalDateTime enrolledTime;
    private EnrollmentStatus status;

    private Double finalGrade;
    private String letterGrade;

}
