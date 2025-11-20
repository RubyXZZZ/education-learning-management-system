package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.listItem.EnrollmentList;
import io.rubyxzzz.lms.backend.dto.response.EnrollmentRes;
import io.rubyxzzz.lms.backend.model.CourseSection;
import io.rubyxzzz.lms.backend.model.Enrollment;
import io.rubyxzzz.lms.backend.repository.CourseRepo;
import io.rubyxzzz.lms.backend.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EnrollmentMapper {

    /**
     * Convert Enrollment entity to detailed response DTO
     */
    public EnrollmentRes toResponse(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        EnrollmentRes response = new EnrollmentRes();
        BeanUtils.copyProperties(enrollment, response);

        // Student info
        if (enrollment.getStudent() != null) {
            response.setStudentId(enrollment.getStudent().getId());
            response.setStudentName(enrollment.getStudent().getFullName());
        }

        // Section info
        CourseSection section = enrollment.getSection();
        if (section != null) {
            response.setCourseSectionId(section.getId());

            // denormalized fields
            response.setSectionCode(section.getSectionCode());
            response.setCourseCode(section.getCourseCode());
            response.setCourseName(section.getCourseName());
            response.setSessionCode(section.getSessionCode());
            response.setInstructorName(section.getInstructorName());
            response.setInstructorEmail(section.getInstructorEmail());
        }

        return response;
    }

    /**
     * Convert Enrollment entity to list item DTO (simplified)
     */
    public EnrollmentList toListItem(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        EnrollmentList item = new EnrollmentList();
        BeanUtils.copyProperties(enrollment, item);

        return item;
    }

    /**
     * Convert list of Enrollment entities to list of response DTOs
     */
    public List<EnrollmentRes> toResponseList(List<Enrollment> enrollments) {
        if (enrollments == null) {
            return List.of();
        }

        return enrollments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of Enrollment entities to list of list item DTOs
     */
    public List<EnrollmentList> toListItems(List<Enrollment> enrollments) {
        if (enrollments == null) {
            return List.of();
        }

        return enrollments.stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
    }
}