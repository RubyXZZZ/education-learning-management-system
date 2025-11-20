package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.response.CoursePageRes;
import io.rubyxzzz.lms.backend.model.CoursePage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoursePageMapper {

    //Convert CoursePage entity to response DTO
    public CoursePageRes toResponse(CoursePage page) {
        if (page == null) {
            return null;
        }

        CoursePageRes response = new CoursePageRes();
        BeanUtils.copyProperties(page, response);

        // Add section ID
        if (page.getCourseSection() != null) {
            response.setCourseSectionId(page.getCourseSection().getId());
        }

        // Add module info (optional)
        if (page.getModule() != null) {
            response.setModuleId(page.getModule().getId());
            response.setModuleName(page.getModule().getName());
        }

        return response;
    }

    // Convert list of CoursePage entities to response DTOs
    public List<CoursePageRes> toResponseList(List<CoursePage> pages) {
        if (pages == null) {
            return List.of();
        }

        return pages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
