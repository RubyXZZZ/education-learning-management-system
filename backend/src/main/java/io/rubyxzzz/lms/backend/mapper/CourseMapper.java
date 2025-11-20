package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.response.CourseRes;
import io.rubyxzzz.lms.backend.model.Course;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final SectionMapper sectionMapper;

    /**
     * Convert Course entity to detailed response DTO
     */
    public CourseRes toResponse(Course course) {
        if (course == null) return null;

        CourseRes response = new CourseRes();
        BeanUtils.copyProperties(course, response);


        if (course.getSession() != null) {
            response.setSessionId(course.getSession().getId());
            response.setSessionCode(course.getSessionCode());
        }

        if (course.getSections() != null) {
            response.setSectionsCount(course.getSections().size());
        } else {
            response.setSectionsCount(0);
        }

        return response;
    }

    /**
     * Convert Course entity to detailed response DTO with sections
     */
    public CourseRes toResponseWithSections(Course course) {
        if (course == null) return null;

        CourseRes response = toResponse(course);

        // Map sections
        if (course.getSections() != null) {
            response.setSections(
                    sectionMapper.toResponseList(course.getSections())
            );
        }

        return response;
    }

    /**
     * Convert list of Course entities to response DTOs
     */
    public List<CourseRes> toResponseList(List<Course> courses) {
        if (courses == null) {
            return List.of();
        }

        return courses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}