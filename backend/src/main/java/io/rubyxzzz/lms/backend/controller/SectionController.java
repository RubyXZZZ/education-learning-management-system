package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.listItem.CourseSectionList;
import io.rubyxzzz.lms.backend.dto.request.CreateSectionReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateSectionReq;
import io.rubyxzzz.lms.backend.dto.response.SectionRes;
import io.rubyxzzz.lms.backend.model.CourseSectionStatus;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.model.UserRole;
import io.rubyxzzz.lms.backend.service.CourseSectionService;
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
 * Course Section REST Controller
 * Handles course section management endpoints
 */
@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {
    private final CourseSectionService sectionService;

    /**
     * Create new course section
     * POST /api/sections
     */
    @PreAuthorize("hasAuthority('SECTIONS_CREATE')")
    @PostMapping
    public ResponseEntity<SectionRes> createSection(
            @Valid @RequestBody CreateSectionReq request) {

        SectionRes section = sectionService.createSection(request);
        return new ResponseEntity<>(section, HttpStatus.CREATED);
    }

    /**
     * Get section by UUID
     * GET /api/sections/{id}
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<SectionRes> getSection(@PathVariable String id) {
        SectionRes section = sectionService.getSection(id);
        return ResponseEntity.ok(section);
    }

//    /**
//     * Get section by course and section code
//     * GET /api/sections/course/{courseId}/code/{sectionCode}
//     */
//    @GetMapping("/course/{courseId}/code/{sectionCode}")
//    public ResponseEntity<SectionRes> getSectionByCourseAndCode(
//            @PathVariable String courseId,
//            @PathVariable String sectionCode) {
//        SectionRes section = sectionService.getSectionByCourseAndCode(
//                courseId, sectionCode
//        );
//        return ResponseEntity.ok(section);
//    }

    /**
     * Get all sections (detailed)
     * GET /api/sections/all
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/all")
    public ResponseEntity<List<SectionRes>> getAllSections() {
        List<SectionRes> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

//    /**
//     * Get sections list (simplified)
//     * GET /api/sections
//     */
//    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
//    @GetMapping
//    public ResponseEntity<List<CourseSectionList>> getSectionsList() {
//        List<CourseSectionList> sections = sectionService.getSectionsList();
//        return ResponseEntity.ok(sections);
//    }

    /**
     * Get sections by course template
     * GET /api/sections/course/{courseUUID}
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseSectionList>> getSectionsByCourse(
            @PathVariable String courseId) {
        List<CourseSectionList> sections = sectionService.getSectionsByCourse(
                courseId
        );
        return ResponseEntity.ok(sections);
    }

    /**
     * Get sections by session UUID
     * GET /api/sections/session/{sessionUUID}
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/session/{sessionUUID}")
    public ResponseEntity<List<SectionRes>> getSectionsBySession(
            @PathVariable String sessionUUID) {
        List<SectionRes> sections = sectionService.getSectionsBySession(
                sessionUUID
        );
        return ResponseEntity.ok(sections);
    }



    /**
     * Get sections by instructor
     * GET /api/sections/instructor/{id}
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/instructor/{id}")
    public ResponseEntity<List<SectionRes>> getSectionsByInstructor(
            @PathVariable String id) {
        List<SectionRes> sections = sectionService.getSectionsByInstructor(
                id
        );
        return ResponseEntity.ok(sections);
    }


    /**
     * Get open sections (available for enrollment)
     * GET /api/sections/enrollable
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/enrollable")
    public ResponseEntity<List<SectionRes>> getEnrollableSections() {
        return ResponseEntity.ok(sectionService.getEnrollableSections());
    }


    /**
     * Update section
     * PUT /api/sections/{id}
     */
    @PreAuthorize("hasAuthority('SECTIONS_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<SectionRes> updateSection(
            @PathVariable String id,
            @Valid @RequestBody UpdateSectionReq request) {

        SectionRes section = sectionService.updateSection(id, request);
        return ResponseEntity.ok(section);
    }

    /**
     * Publish section (make it open for enrollment)
     * POST /api/sections/{id}/publish
     */
    @PreAuthorize("hasAuthority('SECTIONS_EDIT')")
    @PostMapping("/{id}/publish")
    public ResponseEntity<SectionRes> publishSection(@PathVariable String id) {

        SectionRes section = sectionService.publishSection(id);
        return ResponseEntity.ok(section);
    }



    /**
     * Cancel section
     * POST /api/sections/{id}/cancel
     */
    @PreAuthorize("hasAuthority('SECTIONS_EDIT')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<SectionRes> cancelSection(@PathVariable String id) {

        SectionRes section = sectionService.cancelSection(id);
        return ResponseEntity.ok(section);
    }

    /**
     * Delete section
     * DELETE /api/sections/{id}
     */
    @PreAuthorize("hasAuthority('SECTIONS_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable String id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get sections by status
     * GET /api/sections/status/{status}
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SectionRes>> getSectionsByStatus(
            @PathVariable CourseSectionStatus status) {
        List<SectionRes> sections = sectionService.getSectionsByStatus(status);
        return ResponseEntity.ok(sections);
    }


    /**
     * Get sections by instructor and session
     * GET /api/sections/instructor/{instructorId}/session/{sessionId}
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/instructor/{instructorId}/session/{sessionId}")
    public ResponseEntity<List<SectionRes>> getSectionsByInstructorAndSession(
            @PathVariable String instructorId,
            @PathVariable String sessionId) {
        List<SectionRes> sections = sectionService.getSectionsByInstructorAndSession(
                instructorId, sessionId
        );
        return ResponseEntity.ok(sections);
    }

    /**
     * Get full sections (capacity reached)
     * GET /api/sections/full
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/full")
    public ResponseEntity<List<SectionRes>> getFullSections() {
        List<SectionRes> sections = sectionService.getFullSections();
        return ResponseEntity.ok(sections);
    }

    /**
     * Get underfull sections (below minimum enrollment)
     * GET /api/sections/underfull
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/underfull")
    public ResponseEntity<List<SectionRes>> getUnderfullSections() {
        List<SectionRes> sections = sectionService.getUnderfullSections();
        return ResponseEntity.ok(sections);
    }

    /**
     * Get current instructor's teaching sections
     * GET /api/sections/me
     */
    @PreAuthorize("hasAuthority('SECTIONS_VIEW')")
    @GetMapping("/me")
    public ResponseEntity<List<SectionRes>> getMySections(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getUserRole() != UserRole.INSTRUCTOR) {
            return ResponseEntity.status(403)
                    .body(Collections.emptyList());
        }

        List<SectionRes> sections =
                sectionService.getSectionsByInstructor(currentUser.getId());
        return ResponseEntity.ok(sections);
    }


}
