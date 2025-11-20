package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.CourseFormat;
import io.rubyxzzz.lms.backend.model.CourseSectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSectionReq {

    private CourseFormat courseFormat;
    private String schedule;
    private String daysOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;

    private String instructorId;

    private Integer capacity;
    private Integer minEnrollment;


    private CourseSectionStatus status;
    private Boolean enrollmentLocked;
}
