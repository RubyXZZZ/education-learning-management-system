package io.rubyxzzz.lms.backend.service;


import io.rubyxzzz.lms.backend.constants.BusinessConstants;
import io.rubyxzzz.lms.backend.dto.request.CreateCourseReq;
import io.rubyxzzz.lms.backend.dto.request.CreateSectionReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateCourseReq;
import io.rubyxzzz.lms.backend.dto.response.CourseRes;
import io.rubyxzzz.lms.backend.dto.response.SectionRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.CourseMapper;
import io.rubyxzzz.lms.backend.mapper.SectionMapper;
import io.rubyxzzz.lms.backend.model.Course;
import io.rubyxzzz.lms.backend.model.CourseSection;
import io.rubyxzzz.lms.backend.model.Instructor;
import io.rubyxzzz.lms.backend.model.Session;
import io.rubyxzzz.lms.backend.repository.CourseRepo;


import io.rubyxzzz.lms.backend.repository.InstructorRepo;
import io.rubyxzzz.lms.backend.repository.SectionRepo;
import io.rubyxzzz.lms.backend.repository.SessionRepo;
import io.rubyxzzz.lms.backend.util.UpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Course Service
 * Business logic for course management
 *
 * Responsibilities:
 * - Course CRUD operations
 * - Enrollment management
 * - Business rule validation
 * - Instructor assignment
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;
    private final SessionRepo sessionRepo;
    private final CourseMapper courseMapper;
    private final SectionRepo sectionRepo;
    private final InstructorRepo instructorRepo;
    private final SectionMapper sectionMapper;

    /**
     * Create a new course template
     */
    public CourseRes createCourse(CreateCourseReq request) {

        Session session = sessionRepo.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session", request.getSessionId()));


        // Build course entity
        Course course = new Course();

        BeanUtils.copyProperties(request, course);
        course.setSession(session);
        course.setSessionCode(session.getSessionCode());

        // Set defaults if not provided
        if (course.getIsActive() == null) {
            course.setIsActive(true);
        }

        // allowHigherPlacement false by default
        if (course.getAllowHigherPlacement() == null) {
            course.setAllowHigherPlacement(false);
        }

        // Save course template
        Course savedCourse = courseRepo.save(course);

        return courseMapper.toResponse(savedCourse);
    }

    /**
     * Update course template
     */
    public CourseRes updateCourse(String courseId, UpdateCourseReq request) {
        Course course = courseRepo.findByIdWithRelations(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        // Update fields (only non-null values)
        UpdateUtil.copyNonNullProperties(request, course);


        Course updatedCourse = courseRepo.save(course);
        return courseMapper.toResponse(updatedCourse);
    }

    /**
     * Get course by UUID
     */
    public CourseRes getCourse(String courseId) {
        Course course = courseRepo.findByIdWithRelations(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        return courseMapper.toResponse(course);
    }

    /**
     * Get course by course code
     */
    public CourseRes getCourseByCourseCode(String courseCode) {
        Course course = courseRepo.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course", "courseCode", courseCode
                ));
        return courseMapper.toResponse(course);
    }

    /**
     * Get all courses
     */
    public List<CourseRes> getAllCourses() {
        return courseMapper.toResponseList(courseRepo.findAllWithRelations());
    }


    /**
     * Get courses by session
     */
    public List<CourseRes> getCoursesBySession(String sessionId) {
        return courseMapper.toResponseList(
                courseRepo.findBySessionIdWithRelations(sessionId)
        );
    }


    /**
     * Get active courses only
     */
    public List<CourseRes> getActiveCourses() {
        return courseMapper.toResponseList(courseRepo.findActiveCoursesWithRelations());
    }


    /**
     * Get courses by session code
     */
    public List<CourseRes> getCoursesBySessionCode(String sessionCode) {
        return courseMapper.toResponseList(
                courseRepo.findBySessionCodeWithRelations(sessionCode)
        );
    }



    /**
     * Deactivate course (soft delete)
     */
    @Transactional
    public CourseRes deactivateCourse(String courseId) {
        Course course = courseRepo.findByIdWithRelations(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        course.setIsActive(false);
        Course updatedCourse = courseRepo.save(course);
        return courseMapper.toResponse(updatedCourse);
    }

    /**
     * Activate course
     */
    @Transactional
    public CourseRes activateCourse(String courseId) {
        Course course = courseRepo.findByIdWithRelations(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        course.setIsActive(true);
        Course updatedCourse = courseRepo.save(course);
        return courseMapper.toResponse(updatedCourse);
    }


    /**
     * Delete course (hard delete)
     * Only allowed if no sections exist
     */

    @Transactional
    public void deleteCourse(String courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        // Use count query (more efficient)
        long sectionCount = sectionRepo.countByCourseId(courseId);
        if (sectionCount > 0) {
            throw new IllegalStateException(
                    "Cannot delete course with existing sections. " +
                            "Please delete all sections first or set course to inactive."
            );
        }

        courseRepo.delete(course);
    }


    /**
     * Add section to course
     */
    @Transactional
    public SectionRes addSectionToCourse(
            String courseId,
            CreateSectionReq request
    ) {
        // Find Course
        Course course = courseRepo.findByIdWithRelations(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        // Find Instructor
        Instructor instructor = instructorRepo.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", request.getInstructorId()));

        // Create Section
        CourseSection section = new CourseSection();
        BeanUtils.copyProperties(request, section);

        // Set Instructor
        section.setInstructor(instructor);

        // Copy info from Course
        section.setCourseCode(course.getCourseCode());
        section.setCourseName(course.getCourseName());
        section.setSessionCode(course.getSession().getSessionCode());

        // Add Section to Course
        course.addSection(section);
        courseRepo.save(course);

        // Mark instructor assigned
        instructor.assignedCourse();
        instructorRepo.save(instructor);

        return sectionMapper.toResponse(section);
    }

    /**
     * Remove section from course
     */
    @Transactional
    public void removeSectionFromCourse(String courseId, String sectionId) {
        Course course = courseRepo.findByIdWithRelations(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        CourseSection section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseSection", sectionId));

        // Validate
        if (section.getEnrolledCount() > 0) {
            throw new IllegalStateException("Cannot remove section with active enrollments");
        }

        // Unassign instructor
        Instructor instructor = section.getInstructor();
        if (instructor != null) {
            instructor.removeCourse();
            instructorRepo.save(instructor);
        }

        // Delete section
        course.removeSection(section);
        courseRepo.save(course);
    }
}