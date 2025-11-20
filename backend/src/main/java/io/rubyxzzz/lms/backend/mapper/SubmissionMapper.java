package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.response.SubmissionRes;
import io.rubyxzzz.lms.backend.model.Assignment;
import io.rubyxzzz.lms.backend.model.Student;
import io.rubyxzzz.lms.backend.model.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubmissionMapper {
    public SubmissionRes toResponse(Submission submission) {
        if (submission == null) {
            return null;
        }

        SubmissionRes response = new SubmissionRes();
        BeanUtils.copyProperties(submission, response);

        // Add assignment info
        Assignment assignment = submission.getAssignment();
        if (assignment != null) {
            response.setAssignmentId(assignment.getId());
            response.setAssignmentTitle(assignment.getTitle());
            response.setTotalPoints(assignment.getTotalPoints());
            response.setDueDate(assignment.getDueDate());
        }

        // Add student info
        Student student = submission.getStudent();
        if (student != null) {
            response.setStudentId(student.getId());
            response.setStudentNumber(student.getStudentNumber());
            response.setStudentName(student.getFullName());
            response.setStudentEmail(student.getEmail());
        }

        return response;
    }

    public List<SubmissionRes> toResponseList(List<Submission> submissions) {
        if (submissions == null) {
            return List.of();
        }

        return submissions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
