package io.rubyxzzz.lms.backend.dto.listItem;

import io.rubyxzzz.lms.backend.model.CourseFormat;
import io.rubyxzzz.lms.backend.model.CourseSectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionList {
    private String id;
    private String sectionCode;      // "A", "B"

    private String courseId;       // "ESL-LS-L1"
    private String courseCode;     // "ESL-LS"
    private String courseName;       // "Listening & Speaking"
    private Integer hoursPerWeek;    // 7

    private String sessionCode;      // "2025-S1

    private CourseFormat courseFormat;
    private String schedule;         // "Mon/Wed 10:00-11:30"
    private String location;

    private String instructorId;
    private String instructorName;

    private Integer capacity;
    private Integer enrolledCount;
    private Integer availableSeats;  // Calculated

    private CourseSectionStatus status;
    private Boolean enrollmentLocked;
    private Boolean openForEnrollment;  // Calculated
}
