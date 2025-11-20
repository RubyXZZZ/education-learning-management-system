package io.rubyxzzz.lms.backend.controller;


import io.rubyxzzz.lms.backend.dto.listItem.StudentList;
import io.rubyxzzz.lms.backend.dto.request.CreateStudentReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateStudentReq;
import io.rubyxzzz.lms.backend.dto.response.StudentRes;
import io.rubyxzzz.lms.backend.model.StudentType;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.model.UserStatus;
import io.rubyxzzz.lms.backend.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Student REST Controller
 * Handles all student-related HTTP requests
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;


    /**
     * Create student (admin direct creation)
     * POST /api/students
     */
    @PreAuthorize("hasAuthority('STUDENTS_CREATE')")
    @PostMapping
    public ResponseEntity<StudentRes> createStudent(
            @Valid @RequestBody CreateStudentReq request) {

        StudentRes student = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

//    /**
//     * Process placement test
//     * POST /api/students/{id}/placement-test
//     */
//    @PostMapping("/{id}/placement-test")
//    public ResponseEntity<StudentRes> processPlacementTest(
//            @PathVariable String id,
//            @RequestParam Integer placementLevel) {
//
//        StudentRes student = studentService.processPlacementTest(
//                id,
//                placementLevel
//        );
//        return ResponseEntity.ok(student);
//    }

    /**
     * Get students by type
     * GET /api/students/type/{studentType}
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping("/type/{studentType}")
    public ResponseEntity<List<StudentRes>> getStudentsByType(@PathVariable StudentType studentType) {
        List<StudentRes> students = studentService.getStudentsByType(studentType);
        return ResponseEntity.ok(students);
    }


    /**
     * Get student by UUID
     * GET /api/students/{id}
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<StudentRes> getStudent(@PathVariable String id) {
        StudentRes student = studentService.getStudent(id);
        return ResponseEntity.ok(student);
    }


    /**
     * Get student by student number
     * GET /api/students/by-student-number/{studentNumber}
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping("/by-student-number/{studentNumber}")
    public ResponseEntity<StudentRes> getStudentByStudentNumber(@PathVariable String studentNumber) {
        StudentRes student = studentService.getStudentByStudentNumber(studentNumber);
        return ResponseEntity.ok(student);
    }

    /**
     * Get all students (detailed)
     * GET /api/students/all
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping("/all")
    public ResponseEntity<List<StudentRes>> getAllStudents() {
        List<StudentRes> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * Get students list (simplified for table view)
     * GET /api/students
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping
    public ResponseEntity<List<StudentList>> getStudentsList() {
        List<StudentList> students = studentService.getStudentsList();
        return ResponseEntity.ok(students);
    }


    /**
     * Get students by status
     * GET /api/students/status/{status}
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentRes>> getStudentsByStatus(
            @PathVariable UserStatus status) {
        List<StudentRes> students = studentService.getStudentsByStatus(status);
        return ResponseEntity.ok(students);
    }

    /**
     * Get students by current level
     * GET /api/students/level/{levelNumber}
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping("/level/{placementLevel}")
    public ResponseEntity<List<StudentRes>> getStudentsByPlacementLevel(
            @PathVariable Integer placementLevel) {
        List<StudentRes> students = studentService.getStudentsByPlacementLevel(placementLevel);
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/students/{id}/with-enrollments
     * Get student with all enrollments (history)
     * Frontend filters by session/status as needed
     */
    @PreAuthorize("hasAuthority('STUDENTS_VIEW')")
    @GetMapping("/{id}/with-enrollments")
    public ResponseEntity<StudentRes> getStudentWithEnrollments(@PathVariable String id) {
        StudentRes student = studentService.getStudentWithEnrollments(id);
        return ResponseEntity.ok(student);
    }



    /**
     * Update student
     * PUT /api/students/{id}
     */
    @PreAuthorize("hasAuthority('STUDENTS_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentRes> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody UpdateStudentReq request) {

        StudentRes student = studentService.updateStudent(
                id,
                request
        );
        return ResponseEntity.ok(student);
    }

//    /**
//     * Verify email
//     * POST /api/students/{id}/verify-email
//     */
//    @PreAuthorize("hasAuthority('STUDENTS_EDIT')")
//    @PostMapping("/{id}/verify-email")
//    public ResponseEntity<StudentRes> verifyEmail(
//            @PathVariable String id) {
//
//        StudentRes student = studentService.verifyEmail(id);
//        return ResponseEntity.ok(student);
//    }

    /**
     * Suspend student
     * POST /api/students/{id}/suspend
     */
    @PreAuthorize("hasAuthority('STUDENTS_EDIT')")
    @PostMapping("/{id}/suspend")
    public ResponseEntity<StudentRes> suspendStudent(
            @PathVariable String id) {

        StudentRes student = studentService.suspendStudent(id);
        return ResponseEntity.ok(student);
    }

    /**
     * Reactivate student
     * POST /api/students/{id}/reactivate
     */
    @PreAuthorize("hasAuthority('STUDENTS_EDIT')")
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<StudentRes> reactivateStudent(
            @PathVariable String id) {

        StudentRes student = studentService.reactivateStudent(id);
        return ResponseEntity.ok(student);
    }

    /**
     * Deactivate student
     * POST /api/students/{id}/deactivate
     */
    @PreAuthorize("hasAuthority('STUDENTS_EDIT')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<StudentRes> deactivateStudent(
            @PathVariable String id) {

        StudentRes student = studentService.deactivateStudent(id);
        return ResponseEntity.ok(student);
    }

    /**
     * Delete student (hard delete)
     * DELETE /api/students/{id}
     */
    @PreAuthorize("hasAuthority('STUDENTS_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
