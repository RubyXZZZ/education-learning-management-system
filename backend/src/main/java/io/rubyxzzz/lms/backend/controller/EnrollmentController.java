package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.listItem.EnrollmentList;
import io.rubyxzzz.lms.backend.dto.request.BatchEnrollReq;
import io.rubyxzzz.lms.backend.dto.request.CompleteEnrollReq;
import io.rubyxzzz.lms.backend.dto.request.DropCourseReq;
import io.rubyxzzz.lms.backend.dto.request.EnrollCourseReq;
import io.rubyxzzz.lms.backend.dto.response.EnrollmentRes;
import io.rubyxzzz.lms.backend.model.EnrollmentStatus;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.model.UserRole;
import io.rubyxzzz.lms.backend.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


/**
 * Enrollment REST Controller
 * Handles enrollment management endpoints
 *
 * Supports both student and admin operations:
 * - Students can enroll/drop courses
 * - Admins can enroll/drop for students
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Enroll a student in a course section
     * POST /api/enrollments
     *
     * Can be called by:
     * - Student (self-enrollment)
     * - Admin (enrolling student)
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_CREATE')")
    @PostMapping
    public ResponseEntity<EnrollmentRes> enrollCourse(
            @Valid @RequestBody EnrollCourseReq request) {

        EnrollmentRes enrollment = enrollmentService.enrollCourse(request);
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }

//    /**
//     * Admin batch enrollment
//     * POST /api/enrollments/batch
//     */
//    @PostMapping("/batch")
//    public ResponseEntity<List<EnrollmentRes>> batchEnroll(
//            @Valid @RequestBody BatchEnrollReq request) {
//
//        // TODO: Validate Admin permissions
//        // if (!currentUser.getUserRole().equals(UserRole.ADMIN)) {
//        //     throw new ForbiddenException("Only admins can batch enroll");
//        // }
//
//        List<EnrollmentRes> enrollments = enrollmentService.batchEnrollStudents(
//                request.getStudentId(),
//                request.getCourseSectionId()
//        );
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(enrollments);
//    }

    /**
     * Drop an enrollment
     * POST /api/enrollments/drop
     *
     * Can be called by:
     * - Student (dropping own enrollment)
     * - Admin (dropping student's enrollment)
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_DROP')")
    @PostMapping("/drop")
    public ResponseEntity<EnrollmentRes> dropCourse(
            @Valid @RequestBody DropCourseReq request,
            @AuthenticationPrincipal User currentUser) {

        EnrollmentRes enrollment = enrollmentService.dropCourse(
                request,
                currentUser.getId()
        );
        return ResponseEntity.ok(enrollment);
    }

    /**
     * Complete enrollment with final grade
     * POST /api/enrollments/complete
     * Admin only
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_GRADE')")
    @PostMapping("/complete")
    public ResponseEntity<EnrollmentRes> completeEnrollment(
            @Valid @RequestBody CompleteEnrollReq request) {

        EnrollmentRes enrollment = enrollmentService.completeEnrollment(request);
        return ResponseEntity.ok(enrollment);
    }

//    /**
//     * Record attendance
//     * POST /api/enrollments/{id}/attendance
//     * Admin only
//     */
//    @PostMapping("/{id}/attendance")
//    public ResponseEntity<EnrollmentRes> recordAttendance(
//            @PathVariable String id,
//            @RequestParam boolean present,
//            @RequestParam(defaultValue = "false") boolean late) {
//        EnrollmentRes enrollment = enrollmentService.recordAttendance(
//                id, present, late
//        );
//        return ResponseEntity.ok(enrollment);
//    }


    /**
     * Get enrollment by UUID
     * GET /api/enrollments/{id}
     */
    @PreAuthorize("hasAnyAuthority('ENROLLMENTS_VIEW_ALL', 'ENROLLMENTS_VIEW_SECTION', 'ENROLLMENTS_VIEW_OWN')")
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentRes> getEnrollment(@PathVariable String id) {
        EnrollmentRes enrollment = enrollmentService.getEnrollment(id);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * Get all enrollments (detailed) - for admins
     * GET /api/enrollments/all
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_VIEW_ALL')")
    @GetMapping("/all")
    public ResponseEntity<List<EnrollmentRes>> getAllEnrollments() {
        List<EnrollmentRes> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollments by section - for ADMIN (includes all statuses)
     * GET /api/enrollments/section/{sectionId}/admin
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_VIEW_ALL')")
    @GetMapping("/section/{sectionId}/admin")
    public ResponseEntity<List<EnrollmentRes>> getEnrollmentsBySectionForAdmin(
            @PathVariable String sectionId) {
        List<EnrollmentRes> enrollments = enrollmentService.getAllEnrollmentsBySection(sectionId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollments list (simplified)
     * GET /api/enrollments
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_VIEW_ALL')")
    @GetMapping
    public ResponseEntity<List<EnrollmentList>> getEnrollmentsList() {
        List<EnrollmentList> enrollments = enrollmentService.getEnrollmentsList();
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollments by student
     * GET /api/enrollments/student/{studentId}
     */
    @PreAuthorize("hasAnyAuthority('ENROLLMENTS_VIEW_ALL', 'ENROLLMENTS_VIEW_OWN')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentRes>> getEnrollmentsByStudent(
            @PathVariable String studentId) {
        List<EnrollmentRes> enrollments = enrollmentService.getEnrollmentsByStudent(
                studentId
        );
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get active enrollments by student
     * GET /api/enrollments/student/{studentId}/active
     */
    @PreAuthorize("hasAnyAuthority('ENROLLMENTS_VIEW_ALL', 'ENROLLMENTS_VIEW_OWN')")
    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<List<EnrollmentRes>> getActiveEnrollmentsByStudent(
            @PathVariable String studentId) {
        List<EnrollmentRes> enrollments =
                enrollmentService.getActiveEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollments by course section( for instructor/student to view own)
     * GET /api/enrollments/section/{sectionId}
     */
    @PreAuthorize("hasAnyAuthority( 'ENROLLMENTS_VIEW_SECTION')")
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<EnrollmentRes>> getEnrollmentsBySection(
            @PathVariable String sectionId,
            @AuthenticationPrincipal User currentUser) {
        List<EnrollmentRes> enrollments = enrollmentService.getEnrollmentsBySection(
                sectionId,
                currentUser.getId()
        );
        return ResponseEntity.ok(enrollments);
    }


    /**
     * Get enrollments by student and session
     * GET /api/enrollments/student/{studentNumber}/session/{sessionCode}
     */
    @PreAuthorize("hasAnyAuthority('ENROLLMENTS_VIEW_ALL', 'ENROLLMENTS_VIEW_OWN')")
    @GetMapping("/student/{studentNumber}/session/{sessionCode}")
    public ResponseEntity<List<EnrollmentRes>> getEnrollmentsByStudentAndSession(
            @PathVariable String studentNumber,
            @PathVariable String sessionCode) {
        List<EnrollmentRes> enrollments =
                enrollmentService.getEnrollmentsByStudentAndSession(
                        studentNumber, sessionCode
                );
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get completed enrollments by student
     * GET /api/enrollments/student/{studentId}/completed
     */
    @PreAuthorize("hasAnyAuthority('ENROLLMENTS_VIEW_ALL', 'ENROLLMENTS_VIEW_OWN')")
    @GetMapping("/student/{studentId}/completed")
    public ResponseEntity<List<EnrollmentRes>> getCompletedEnrollmentsByStudent(
            @PathVariable String studentId) {
        List<EnrollmentRes> enrollments =
                enrollmentService.getCompletedEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollments by status
     * GET /api/enrollments/status/{status}
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_VIEW_ALL')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EnrollmentRes>> getEnrollmentsByStatus(
            @PathVariable EnrollmentStatus status) {
        List<EnrollmentRes> enrollments = enrollmentService.getEnrollmentsByStatus(
                status
        );
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get current student's enrollments
     * GET /api/enrollments/me
     */
    @PreAuthorize("hasAuthority('ENROLLMENTS_VIEW_OWN')")
    @GetMapping("/me")
    public ResponseEntity<List<EnrollmentRes>> getMyEnrollments(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getUserRole() != UserRole.STUDENT) {
            return ResponseEntity.status(403)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByStudent(currentUser.getId())
        );
    }
}
