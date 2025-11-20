package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.listItem.CourseSectionList;
import io.rubyxzzz.lms.backend.dto.response.SectionRes;
import io.rubyxzzz.lms.backend.model.Course;
import io.rubyxzzz.lms.backend.model.CourseSection;
import io.rubyxzzz.lms.backend.repository.CourseRepo;

import io.rubyxzzz.lms.backend.repository.SessionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SectionMapper {

    /**
     * Convert CourseSection entity to detailed response DTO
     */
    public SectionRes toResponse(CourseSection section) {
        if (section == null) return null;

        SectionRes response = new SectionRes();
        BeanUtils.copyProperties(section, response);

        // Add calculated fields
        response.setAvailableSeats(section.getAvailableSeats());
        response.setOpenForEnrollment(section.isOpenForEnrollment());
        response.setMeetsMinimumEnrollment(section.meetsMinimumEnrollment());
        response.setCapacityUtilization(
                section.getCapacity() != null && section.getCapacity() > 0 ?
                        (double) section.getEnrolledCount() / section.getCapacity() * 100 : 0.0
        );

        // Get course template info
        Course course = section.getCourse();
        if (course != null) {
            response.setCourseId(course.getId());
            response.setPrerequisiteCourses(course.getPrerequisiteCourses());
            response.setRequiredPlacementLevel(course.getRequiredPlacementLevel());
            response.setAllowHigherPlacement(course.getAllowHigherPlacement());
            response.setCourseDescription(course.getCourseDescription());
        }

        // Get instructor info
        if (section.getInstructor() != null) {
            response.setInstructorId(section.getInstructor().getId());
            response.setInstructorName(section.getInstructor().getFullName());
            response.setInstructorEmail(section.getInstructor().getEmail());
        }


        return response;
    }

    /**
     * Convert CourseSection entity to list item DTO
     */
    public CourseSectionList toListItem(CourseSection section) {
        if (section == null) return null;

        CourseSectionList item = new CourseSectionList();
        BeanUtils.copyProperties(section, item);

        if (section.getCourse() != null) {
            item.setCourseId(section.getCourse().getId());
        }

        if (section.getInstructor() != null) {
            item.setInstructorId(section.getInstructor().getId());
            item.setInstructorName(section.getInstructor().getFullName());
        }

        // Add calculated fields
        item.setAvailableSeats(section.getAvailableSeats());
        item.setOpenForEnrollment(section.isOpenForEnrollment());

        return item;
    }

    /**
     * Convert list of CourseSection entities to response DTOs
     */
    public List<SectionRes> toResponseList(List<CourseSection> sections) {
        if (sections == null) {
            return List.of();
        }

        return sections.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of CourseSection entities to list item DTOs
     */
    public List<CourseSectionList> toListItems(List<CourseSection> sections) {
        if (sections == null) {
            return List.of();
        }

        return sections.stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
    }
}