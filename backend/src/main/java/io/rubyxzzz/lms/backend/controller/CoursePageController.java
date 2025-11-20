package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.request.CreateCoursePageReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateCoursePageReq;
import io.rubyxzzz.lms.backend.dto.response.CoursePageRes;
import io.rubyxzzz.lms.backend.service.CoursePageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class CoursePageController {

    private final CoursePageService coursePageService;

    /**
     * Create a new page
     * POST /api/pages
     */
    @PreAuthorize("hasAuthority('PAGES_CREATE')")
    @PostMapping
    public ResponseEntity<CoursePageRes> createCoursePage(
            @Valid @RequestBody CreateCoursePageReq request
    ) {
        CoursePageRes response = coursePageService.createCoursePage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get page by ID
     * GET /api/pages/{id}
     */
    @PreAuthorize("hasAnyAuthority('PAGES_VIEW_ALL', 'PAGES_VIEW_PUBLISHED')")
    @GetMapping("/{id}")
    public ResponseEntity<CoursePageRes> getCoursePage(@PathVariable String id) {
        CoursePageRes response = coursePageService.getCoursePage(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update page
     * PUT /api/pages/{id}
     */
    @PreAuthorize("hasAuthority('PAGES_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<CoursePageRes> updateCoursePage(
            @PathVariable String id,
            @Valid @RequestBody UpdateCoursePageReq request
    ) {
        CoursePageRes response = coursePageService.updateCoursePage(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete page
     * DELETE /api/pages/{id}
     */
    @PreAuthorize("hasAuthority('PAGES_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable String id) {
        coursePageService.deletePage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get pages by section (instructor view - all pages)
     * GET /api/sections/{sectionId}/pages
     */
    @PreAuthorize("hasAuthority('PAGES_VIEW_ALL')")
    @GetMapping("/sections/{sectionId}")
    public ResponseEntity<List<CoursePageRes>> getPagesBySection(
            @PathVariable String sectionId
    ) {
        List<CoursePageRes> response = coursePageService.getPagesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get published pages by section (student view)
     * GET /api/sections/{sectionId}/pages/published
     */
    @PreAuthorize("hasAuthority('PAGES_VIEW_PUBLISHED')")
    @GetMapping("/sections/{sectionId}/published")
    public ResponseEntity<List<CoursePageRes>> getPublishedPagesBySection(
            @PathVariable String sectionId
    ) {
        List<CoursePageRes> response = coursePageService.getPublishedPagesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get pages by module (instructor view - all pages)
     * GET /api/modules/{moduleId}/pages
     */
    @PreAuthorize("hasAuthority('PAGES_VIEW_ALL')")
    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<List<CoursePageRes>> getPagesByModule(
            @PathVariable String moduleId
    ) {
        List<CoursePageRes> response = coursePageService.getPagesByModule(moduleId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get published pages by module (student view)
     * GET /api/modules/{moduleId}/pages/published
     */
    @PreAuthorize("hasAuthority('PAGES_VIEW_PUBLISHED')")
    @GetMapping("/modules/{moduleId}/published")
    public ResponseEntity<List<CoursePageRes>> getPublishedPagesByModule(
            @PathVariable String moduleId
    ) {
        List<CoursePageRes> response = coursePageService.getPublishedPagesByModule(moduleId);
        return ResponseEntity.ok(response);
    }


    /**
     * Publish page
     * POST /api/pages/{id}/publish
     */
    @PreAuthorize("hasAuthority('PAGES_EDIT')")
    @PostMapping("/{id}/publish")
    public ResponseEntity<CoursePageRes> publishPage(@PathVariable String id) {
        CoursePageRes response = coursePageService.publishPage(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Unpublish page
     * POST /api/pages/{id}/unpublish
     */
    @PreAuthorize("hasAuthority('PAGES_EDIT')")
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<CoursePageRes> unpublishPage(@PathVariable String id) {
        CoursePageRes response = coursePageService.unpublishPage(id);
        return ResponseEntity.ok(response);
    }
}
