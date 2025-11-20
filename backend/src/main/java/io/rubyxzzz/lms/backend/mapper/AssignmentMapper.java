package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.response.AssignmentRes;
import io.rubyxzzz.lms.backend.model.Assignment;
import io.rubyxzzz.lms.backend.model.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AssignmentMapper {
    public AssignmentRes toResponse(Assignment assignment) {
        if (assignment == null) {
            return null;
        }

        AssignmentRes response = new AssignmentRes();
        BeanUtils.copyProperties(assignment, response);

        // Add section ID
        if (assignment.getCourseSection() != null) {
            response.setCourseSectionId(assignment.getCourseSection().getId());
        }

        // Add module info (optional)
//        if (assignment.getModule() != null) {
//            response.setModuleId(assignment.getModule().getId());
//        }

        // Add calculated fields
        response.setIsOverdue(assignment.isOverdue());
        response.setAcceptsSubmissions(assignment.acceptsSubmissions());

        // Add submission statistics (from submissions collection)
        if (assignment.getSubmissions() != null) {
            response.setSubmissionCount(assignment.getSubmissions().size());
            response.setGradedCount((int) assignment.getSubmissions().stream()
                    .filter(s -> s.getStatus() == SubmissionStatus.GRADED)
                    .count());
        }

        return response;
    }

    public List<AssignmentRes> toResponseList(List<Assignment> assignments) {
        if (assignments == null) {
            return List.of();
        }

        return assignments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
