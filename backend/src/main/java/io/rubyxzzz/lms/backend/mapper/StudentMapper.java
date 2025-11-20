package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.listItem.StudentList;
import io.rubyxzzz.lms.backend.dto.response.StudentRes;
import io.rubyxzzz.lms.backend.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final EnrollmentMapper enrollmentMapper;

    /**
     * Convert Student entity to detailed response DTO (no enrollments)
     */
    public StudentRes toResponse(Student student) {
        if (student == null) {
            return null;
        }

        StudentRes response = new StudentRes();

        // Copy all matching fields
        BeanUtils.copyProperties(student, response);

        // Set calculated fields
        response.setFullName(student.getFullName());
        response.setAge(student.getAge());
        response.setCanEnrollMore(student.canEnrollMore());
        //response.setEnrollmentMode(student.getEnrollmentMode());
        response.setMaxHoursAllowed(student.getMaxHoursAllowed());
        response.setMinHoursRequired(student.getMinHoursRequired());

        // Calculate pass rate
//        if (student.getTotalCoursesCompleted() != null &&
//                student.getTotalCoursesCompleted() > 0) {
//            Integer passed = student.getTotalCoursesPassed() != null ?
//                    student.getTotalCoursesPassed() : 0;
//            Double passRate = (double) passed / student.getTotalCoursesCompleted() * 100;
//            response.setPassRate(passRate);
//        } else {
//            response.setPassRate(0.0);
//        }

        return response;
    }

    /**
     * Convert Student to response with enrollments
     * Use this when student.getEnrollments() is already loaded
     */
    public StudentRes toResponseWithEnrollments(Student student) {
        if (student == null) {
            return null;
        }

        StudentRes response = new StudentRes();

        // Copy all matching fields
        BeanUtils.copyProperties(student, response);

        // Set calculated fields
        response.setFullName(student.getFullName());
        response.setAge(student.getAge());
        response.setCanEnrollMore(student.canEnrollMore());
        //response.setEnrollmentMode(student.getEnrollmentMode());
        response.setMaxHoursAllowed(student.getMaxHoursAllowed());
        response.setMinHoursRequired(student.getMinHoursRequired());

        // Calculate pass rate
//        if (student.getTotalCoursesCompleted() != null &&
//                student.getTotalCoursesCompleted() > 0) {
//            Integer passed = student.getTotalCoursesPassed() != null ?
//                    student.getTotalCoursesPassed() : 0;
//            Double passRate = (double) passed / student.getTotalCoursesCompleted() * 100;
//            response.setPassRate(passRate);
//        } else {
//            response.setPassRate(0.0);
//        }

        if (student.getEnrollments() != null) {
            response.setEnrollments(
                    enrollmentMapper.toResponseList(student.getEnrollments())
            );
        }

        return response;
    }

    /**
     * Convert Student entity to list item DTO (simplified)
     */
    public StudentList toListItem(Student student) {
        if (student == null) {
            return null;
        }

        StudentList item = new StudentList();
        BeanUtils.copyProperties(student, item);

        // Set calculated field
        item.setFullName(student.getFullName());

        return item;
    }

    /**
     * Convert list of Student entities to response DTOs
     */
    public List<StudentRes> toResponseList(List<Student> students) {
        if (students == null) {
            return List.of();
        }

        return students.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of Student entities to list item DTOs
     */
    public List<StudentList> toListItems(List<Student> students) {
        if (students == null) {
            return List.of();
        }

        return students.stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
    }
}