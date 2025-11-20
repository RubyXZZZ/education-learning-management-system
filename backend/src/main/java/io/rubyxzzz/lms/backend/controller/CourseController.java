package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.request.CreateCourseReq;
import io.rubyxzzz.lms.backend.dto.request.CreateSectionReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateCourseReq;
import io.rubyxzzz.lms.backend.dto.response.CourseRes;
import io.rubyxzzz.lms.backend.dto.response.SectionRes;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Course REST Controller
 * Handles course management endpoints
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Create new course template
     * POST /api/courses
     */
    @PreAuthorize("hasAuthority('COURSES_CREATE')")
    @PostMapping
    public ResponseEntity<CourseRes> createCourse(
            @Valid @RequestBody CreateCourseReq request) {

        CourseRes course = courseService.createCourse(request);
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }

    /**
     * Get course by UUID
     * GET /api/courses/{id}
     */
    @PreAuthorize("hasAuthority('COURSES_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseRes> getCourse(@PathVariable String id) {
        CourseRes course = courseService.getCourse(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Get course by course code
     * GET /api/courses/by-code/{courseCode}
     */
    @PreAuthorize("hasAuthority('COURSES_VIEW')")
    @GetMapping("/by-code/{courseCode}")
    public ResponseEntity<CourseRes> getCourseByCourseCode(
            @PathVariable String courseCode) {
        CourseRes course = courseService.getCourseByCourseCode(courseCode);
        return ResponseEntity.ok(course);
    }

    /**
     * Get all courses (detailed)
     * GET /api/courses/all
     */
    @PreAuthorize("hasAuthority('COURSES_VIEW')")
    @GetMapping("/all")
    public ResponseEntity<List<CourseRes>> getAllCourses() {
        List<CourseRes> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }



    /**
     * Get active courses only
     * GET /api/courses/active
     */
    @PreAuthorize("hasAuthority('COURSES_VIEW')")
    @GetMapping("/active")
    public ResponseEntity<List<CourseRes>> getActiveCourses() {
        List<CourseRes> courses = courseService.getActiveCourses();
        return ResponseEntity.ok(courses);
    }


    /**
     * Update course template
     * PUT /api/courses/{id}
     */
    @PreAuthorize("hasAuthority('COURSES_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseRes> updateCourse(
            @PathVariable String id,
            @Valid @RequestBody UpdateCourseReq request) {

        CourseRes course = courseService.updateCourse(id, request);
        return ResponseEntity.ok(course);
    }

    /**
     * Deactivate course (soft delete)
     * POST /api/courses/{id}/deactivate
     */
    @PreAuthorize("hasAuthority('COURSES_EDIT')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<CourseRes> deactivateCourse(@PathVariable String id) {

        CourseRes course = courseService.deactivateCourse(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Activate course
     * POST /api/courses/{id}/activate
     */
    @PreAuthorize("hasAuthority('COURSES_EDIT')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<CourseRes> activateCourse(@PathVariable String id) {

        CourseRes course = courseService.activateCourse(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Delete course (hard delete)
     * DELETE /api/courses/{uuid}
     */
    @PreAuthorize("hasAuthority('COURSES_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add section to course
     * POST /api/courses/{courseId}/sections
     */
    @PreAuthorize("hasAuthority('COURSES_EDIT')")
    @PostMapping("/{courseId}/sections")
    public ResponseEntity<SectionRes> addSection(
            @PathVariable String courseId,
            @Valid @RequestBody CreateSectionReq request) {

        SectionRes section = courseService.addSectionToCourse(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(section);
    }

    /**
     * Remove section from course
     * DELETE /api/courses/{courseId}/sections/{sectionId}
     */
    @PreAuthorize("hasAuthority('COURSES_EDIT')")
    @DeleteMapping("/{courseId}/sections/{sectionId}")
    public ResponseEntity<Void> removeSection(
            @PathVariable String courseId,
            @PathVariable String sectionId) {

        courseService.removeSectionFromCourse(courseId, sectionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get courses by session code
     * GET /api/courses/session-code/{sessionCode}
     */
    @PreAuthorize("hasAuthority('COURSES_VIEW')")
    @GetMapping("/session-code/{sessionCode}")
    public ResponseEntity<List<CourseRes>> getCoursesBySessionCode(
            @PathVariable String sessionCode) {
        List<CourseRes> courses = courseService.getCoursesBySessionCode(sessionCode);
        return ResponseEntity.ok(courses);
    }


    /**
     * Get courses by session (use session ID)
     * GET /api/courses/session/{sessionId}
     */
    @PreAuthorize("hasAuthority('COURSES_VIEW')")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<CourseRes>> getCoursesBySession(
            @PathVariable String sessionId) {
        List<CourseRes> courses = courseService.getCoursesBySession(sessionId);
        return ResponseEntity.ok(courses);
    }
}