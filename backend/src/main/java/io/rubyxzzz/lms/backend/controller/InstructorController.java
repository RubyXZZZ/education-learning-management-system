package io.rubyxzzz.lms.backend.controller;


import io.rubyxzzz.lms.backend.dto.listItem.InstructorList;
import io.rubyxzzz.lms.backend.dto.request.CreateInstructorReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateInstructorReq;
import io.rubyxzzz.lms.backend.dto.response.AdminRes;
import io.rubyxzzz.lms.backend.dto.response.InstructorRes;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.model.UserStatus;
import io.rubyxzzz.lms.backend.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Instructor REST Controller
 * Handles instructor management endpoints
 */
@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    /**
     * Create new instructor
     * POST /api/instructors
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_CREATE')")
    @PostMapping
    public ResponseEntity<InstructorRes> createInstructor(
            @Valid @RequestBody CreateInstructorReq request
            ) {

        InstructorRes instructor = instructorService.createInstructor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(instructor);
    }

    /**
     * Get instructor by UUID
     * GET /api/instructors/{id}
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<InstructorRes> getInstructor(@PathVariable String id) {
        InstructorRes instructor = instructorService.getInstructor(id);
        return ResponseEntity.ok(instructor);
    }

    /**
     * Get all instructors (detailed)
     * GET /api/instructors/all
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_VIEW')")
    @GetMapping("/all")
    public ResponseEntity<List<InstructorRes>> getAllInstructors() {
        List<InstructorRes> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    /**
     * Get instructors list (simplified)
     * GET /api/instructors
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_VIEW')")
    @GetMapping
    public ResponseEntity<List<InstructorList>> getInstructorsList() {
        List<InstructorList> instructors = instructorService.getInstructorsList();
        return ResponseEntity.ok(instructors);
    }

    /**
     * Get instructors by department
     * GET /api/instructors/department/{department}
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_VIEW')")
    @GetMapping("/department/{department}")
    public ResponseEntity<List<InstructorRes>> getInstructorsByDepartment(
            @PathVariable String department) {

        List<InstructorRes> instructors = instructorService.getInstructorsByDepartment(department);
        return ResponseEntity.ok(instructors);
    }

    /**
     * Update instructor information
     * PUT /api/instructors/{id}
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<InstructorRes> updateInstructor(
            @PathVariable String id,
            @Valid @RequestBody UpdateInstructorReq request) {

        InstructorRes instructor = instructorService.updateInstructor(id, request);
        return ResponseEntity.ok(instructor);
    }

//    /**
//     * Verify instructor email
//     * POST /api/instructors/{id}/verify-email
//     */
//    @PreAuthorize("hasAuthority('INSTRUCTORS_EDIT')")
//    @PostMapping("/{id}/verify-email")
//    public ResponseEntity<InstructorRes> verifyEmail(
//            @PathVariable String id) {
//
//        InstructorRes instructor = instructorService.verifyEmail(id);
//        return ResponseEntity.ok(instructor);
//    }

    /**
     * Suspend instructor
     * POST /api/instructors/{id}/suspend
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_EDIT')")
    @PostMapping("/{id}/suspend")
    public ResponseEntity<InstructorRes> suspendInstructor(
            @PathVariable String id) {

        InstructorRes instructor = instructorService.suspendInstructor(id);
        return ResponseEntity.ok(instructor);
    }

    /**
     * Reactivate instructor
     * POST /api/instructors/{id}/reactivate
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_EDIT')")
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<InstructorRes> reactivateInstructor(
            @PathVariable String id) {

        InstructorRes instructor = instructorService.reactivateInstructor(id);
        return ResponseEntity.ok(instructor);
    }

    /**
     * Deactivate instructor
     * POST /api/instructors/{id}/deactivate
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_EDIT')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<InstructorRes> deactivateInstructor(
            @PathVariable String id) {

        InstructorRes instructor = instructorService.deactivateInstructor(id);
        return ResponseEntity.ok(instructor);
    }

    /**
     * Delete instructor (hard delete)
     * DELETE /api/instructors/{id}
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable String id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get instructor by employee number
     * GET /api/instructors/by-employee/{employeeNumber}
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_VIEW')")
    @GetMapping("/by-employee/{employeeNumber}")
    public ResponseEntity<InstructorRes> getInstructorByEmployeeNumber(
            @PathVariable String employeeNumber) {
        InstructorRes instructor = instructorService.getInstructorByEmployeeNumber(employeeNumber);
        return ResponseEntity.ok(instructor);
    }

    /**
     * GET /api/instructors/{id}/with-sections
     * Get instructor with all teaching sections (history)
     * Frontend filters by session/status as needed
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_VIEW')")
    @GetMapping("/{id}/with-sections")
    public ResponseEntity<InstructorRes> getInstructorWithSections(@PathVariable String id) {
        InstructorRes instructor = instructorService.getInstructorWithSections(id);
        return ResponseEntity.ok(instructor);
    }

    /**
     * Get instuctors by status
     * GET /api/instructors/status/{status}
     */
    @PreAuthorize("hasAuthority('INSTRUCTORS_VIEW')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<InstructorRes>> getInstructorsByStatus(
            @PathVariable UserStatus status) {
        List<InstructorRes> instructors = instructorService.getInstructorsByStatus(status);
        return ResponseEntity.ok(instructors);
    }


}