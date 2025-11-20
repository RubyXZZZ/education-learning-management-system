package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.request.CreateSubmReq;
import io.rubyxzzz.lms.backend.dto.request.GradeSubmReq;
import io.rubyxzzz.lms.backend.dto.response.SubmissionRes;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    /**
     * Create or resubmit assignment (unified endpoint)
     * POST /api/submissions
     */
    @PreAuthorize("hasAuthority('SUBMISSIONS_CREATE')")
    @PostMapping
    public ResponseEntity<SubmissionRes> createSubmission(
            @Valid @RequestBody CreateSubmReq request,
            @AuthenticationPrincipal User currentUser
    ) {
        SubmissionRes response = submissionService.createSubmission(
                request,
                currentUser.getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get submission by ID
     * GET /api/submissions/{id}
     */
    @PreAuthorize("hasAnyAuthority('SUBMISSIONS_VIEW_ALL', 'SUBMISSIONS_VIEW_OWN')")
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionRes> getSubmission(@PathVariable String id) {
        SubmissionRes response = submissionService.getSubmission(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Grade submission (instructor only)
     * POST /api/submissions/grade
     */
    @PreAuthorize("hasAuthority('SUBMISSIONS_GRADE')")
    @PostMapping("/grade")
    public ResponseEntity<SubmissionRes> gradeSubmission(
            @Valid @RequestBody GradeSubmReq request,
            @AuthenticationPrincipal User currentUser
    ) {
        SubmissionRes response = submissionService.gradeSubmission(
                request,
                currentUser.getId()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Get all submissions for assignment (including MISSING students)
     * GET /api/assignments/{assignmentId}/submissions
     */
    @PreAuthorize("hasAnyAuthority('SUBMISSIONS_VIEW_ALL')")
    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<List<SubmissionRes>> getSubmissionsWithMissing(
            @PathVariable String assignmentId
    ) {
        List<SubmissionRes> response = submissionService.getSubmissionsWithMissing(assignmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get ungraded submissions (instructor)
     * GET /api/assignments/{assignmentId}/submissions/ungraded
     */
    @PreAuthorize("hasAuthority('SUBMISSIONS_VIEW_ALL')")
    @GetMapping("/assignments/{assignmentId}/ungraded")
    public ResponseEntity<List<SubmissionRes>> getUngradedSubmissions(
            @PathVariable String assignmentId
    ) {
        List<SubmissionRes> response = submissionService.getUngradedSubmissions(assignmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get graded submissions (instructor)
     * GET /api/assignments/{assignmentId}/submissions/graded
     */
    @PreAuthorize("hasAuthority('SUBMISSIONS_VIEW_ALL')")
    @GetMapping("/assignments/{assignmentId}/graded")
    public ResponseEntity<List<SubmissionRes>> getGradedSubmissions(
            @PathVariable String assignmentId
    ) {
        List<SubmissionRes> response = submissionService.getGradedSubmissions(assignmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get submissions by student
     * GET /api/students/{studentId}/submissions
     *
     * Can be used by:
     * - Student: GET /api/students/{myId}/submissions (view own submissions)
     * - Instructor/Admin: View any student's submissions
     */
    @PreAuthorize("hasAnyAuthority('SUBMISSIONS_VIEW_ALL', 'SUBMISSIONS_VIEW_OWN')")
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<SubmissionRes>> getSubmissionsByStudent(
            @PathVariable String studentId
    ) {
        List<SubmissionRes> response = submissionService.getSubmissionsByStudent(studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get student's latest submission for assignment
     * GET /api/assignments/{assignmentId}/students/{studentId}
     *
     * Can be used by:
     * - Student: View own submission
     * - Instructor: View student's submission for grading
     */
    @PreAuthorize("hasAnyAuthority('SUBMISSIONS_VIEW_ALL', 'SUBMISSIONS_VIEW_OWN')")
    @GetMapping("/assignments/{assignmentId}/students/{studentId}")
    public ResponseEntity<SubmissionRes> getStudentLatestSubmission(
            @PathVariable String assignmentId,
            @PathVariable String studentId
    ) {
        SubmissionRes response = submissionService.getStudentLatestSubmission(
                assignmentId, studentId
        );
        return ResponseEntity.ok(response);
    }
}
