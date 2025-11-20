package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.CourseFormat;
import io.rubyxzzz.lms.backend.model.CourseSectionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalTime;

/**
 * Request DTO for creating a new course section
 * Used when opening a course for a specific session
 */
@Data
public class CreateSectionReq {

    @NotBlank(message = "Course UUID is required")
    private String courseId;


    @NotBlank(message = "Section code is required")
    private String sectionCode;



    @NotNull(message = "Course format is required")
    private CourseFormat courseFormat;

    private String schedule;        // "Mon/Wed 10:00-11:30"
    private String daysOfWeek;      // "Monday,Wednesday"
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;        // "Room 201" or "Online"


    @NotBlank(message = "Instructor UUID is required")
    private String instructorId;


    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @Positive(message = "Minimum enrollment must be positive")
    private Integer minEnrollment;

}