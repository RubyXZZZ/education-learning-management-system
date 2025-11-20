package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.request.CreateAsgnReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateAsgnReq;
import io.rubyxzzz.lms.backend.dto.response.AssignmentRes;
import io.rubyxzzz.lms.backend.model.AssignmentType;
import io.rubyxzzz.lms.backend.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    /**
     * Create a new assignment
     * POST /api/assignments
     */
    @PreAuthorize("hasAuthority('ASSIGNMENTS_CREATE')")
    @PostMapping
    public ResponseEntity<AssignmentRes> createAssignment(
            @Valid @RequestBody CreateAsgnReq request
    ) {
        AssignmentRes response = assignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get assignment by ID
     * GET /api/assignments/{id}
     */
    @PreAuthorize("hasAnyAuthority('ASSIGNMENTS_VIEW_ALL', 'ASSIGNMENTS_VIEW_PUBLISHED')")
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentRes> getAssignment(@PathVariable String id) {
        AssignmentRes response = assignmentService.getAssignment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update assignment
     * PUT /api/assignments/{id}
     */
    @PreAuthorize("hasAuthority('ASSIGNMENTS_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentRes> updateAssignment(
            @PathVariable String id,
            @Valid @RequestBody UpdateAsgnReq request
    ) {
        AssignmentRes response = assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete assignment
     * DELETE /api/assignments/{id}
     */
    @PreAuthorize("hasAuthority('ASSIGNMENTS_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get assignments by section (instructor view - all assignments)
     * GET /api/sections/{sectionId}/assignments
     */
    @PreAuthorize("hasAuthority('ASSIGNMENTS_VIEW_ALL')")
    @GetMapping("/sections/{sectionId}")
    public ResponseEntity<List<AssignmentRes>> getAssignmentsBySection(
            @PathVariable String sectionId
    ) {
        List<AssignmentRes> response = assignmentService.getAssignmentsBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get published assignments by section (student view)
     * GET /api/sections/{sectionId}/assignments/published
     */
    @PreAuthorize("hasAuthority('ASSIGNMENTS_VIEW_PUBLISHED')")
    @GetMapping("/sections/{sectionId}/published")
    public ResponseEntity<List<AssignmentRes>> getPublishedAssignmentsBySection(
            @PathVariable String sectionId
    ) {
        List<AssignmentRes> response = assignmentService.getPublishedAssignmentsBySection(sectionId);
        return ResponseEntity.ok(response);
    }

//    /**
//     * Get assignments by type
//     * GET /api/sections/{sectionId}/assignments/type/{type}
//     */
//    @PreAuthorize("hasAuthority('ASSIGNMENTS_VIEW')")
//    @GetMapping("/sections/{sectionId}/type/{type}")
//    public ResponseEntity<List<AssignmentRes>> getAssignmentsBySectionAndType(
//            @PathVariable String sectionId,
//            @PathVariable AssignmentType type
//    ) {
//        List<AssignmentRes> response = assignmentService.getAssignmentsBySectionAndType(sectionId, type);
//        return ResponseEntity.ok(response);
//    }

    /**
     * Get upcoming assignments
     * GET /api/sections/{sectionId}/assignments/upcoming
     */
    @PreAuthorize("hasAnyAuthority('ASSIGNMENTS_VIEW_ALL', 'ASSIGNMENTS_VIEW_PUBLISHED')")
    @GetMapping("/sections/{sectionId}/upcoming")
    public ResponseEntity<List<AssignmentRes>> getUpcomingAssignments(
            @PathVariable String sectionId
    ) {
        List<AssignmentRes> response = assignmentService.getUpcomingAssignments(sectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get overdue assignments
     * GET /api/sections/{sectionId}/assignments/overdue
     */
    @PreAuthorize("hasAnyAuthority('ASSIGNMENTS_VIEW_ALL', 'ASSIGNMENTS_VIEW_PUBLISHED')")
    @GetMapping("/sections/{sectionId}/overdue")
    public ResponseEntity<List<AssignmentRes>> getOverdueAssignments(
            @PathVariable String sectionId
    ) {
        List<AssignmentRes> response = assignmentService.getOverdueAssignments(sectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Publish assignment
     * POST /api/assignments/{id}/publish
     */
    @PreAuthorize("hasAuthority('ASSIGNMENTS_EDIT')")
    @PostMapping("/{id}/publish")
    public ResponseEntity<AssignmentRes> publishAssignment(@PathVariable String id) {
        AssignmentRes response = assignmentService.publishAssignment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Unpublish assignment
     * POST /api/assignments/{id}/unpublish
     */
    @PreAuthorize("hasAuthority('ASSIGNMENTS_EDIT')")
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<AssignmentRes> unpublishAssignment(@PathVariable String id) {
        AssignmentRes response = assignmentService.unpublishAssignment(id);
        return ResponseEntity.ok(response);
    }
}
