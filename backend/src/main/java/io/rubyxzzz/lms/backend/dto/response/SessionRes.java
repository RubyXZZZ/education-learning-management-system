package io.rubyxzzz.lms.backend.dto.response;

import io.rubyxzzz.lms.backend.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionRes {
    private String id;
    private String sessionCode;


    private LocalDate startDate;
    private LocalDate endDate;

    private SessionStatus status;

//    private LocalDate registrationOpenDate;
//    private LocalDate registrationDeadline;
//
//    private LocalDate addDropDeadline;
//    private LocalDate withdrawDeadline;

    private int totalCoursesOffered;
    private int totalEnrollments;

    // Calculated fields
    private boolean active;

    private List<CourseRes> courses;

    private LocalDate createdAt;
    private String createdBy;
    private LocalDate updatedAt;
    private String updatedBy;
}
