package io.rubyxzzz.lms.backend.service;


import io.rubyxzzz.lms.backend.dto.listItem.EnrollmentList;
import io.rubyxzzz.lms.backend.dto.request.CompleteEnrollReq;
import io.rubyxzzz.lms.backend.dto.request.DropCourseReq;
import io.rubyxzzz.lms.backend.dto.request.EnrollCourseReq;
import io.rubyxzzz.lms.backend.dto.response.EnrollmentRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.EnrollmentMapper;
import io.rubyxzzz.lms.backend.model.*;
import io.rubyxzzz.lms.backend.repository.EnrollmentRepo;
import io.rubyxzzz.lms.backend.repository.SectionRepo;
import io.rubyxzzz.lms.backend.repository.StudentRepo;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enrollment Service
 * Business logic for enrollment management
 *
 * Responsibilities:
 * - Enrollment creation (by student or admin)
 * - Drop enrollment (by student or admin)
 * - Academic progress tracking
 * - Attendance recording
 * - Grade completion
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepo enrollmentRepo;
    private final StudentRepo studentRepo;
    private final SectionRepo sectionRepo;
    private final EnrollmentMapper enrollmentMapper;

    /**
     * Enroll a student in a course section
     * Can be initiated by student or admin
     */
    @Transactional
    public EnrollmentRes enrollCourse(EnrollCourseReq request) {
        // Validate student exists
        Student student = studentRepo.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", request.getStudentId()
                ));

        // Validate course section exists and is open
        CourseSection section = sectionRepo.findById(request.getCourseSectionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CourseSection", request.getCourseSectionId()
                ));

        // Check if section is open for enrollment
        if (!section.isOpenForEnrollment()) {
            throw new IllegalStateException(
                    "Course section is not open for enrollment. Status: " +
                            section.getStatus()
            );
        }

        // Check if section has available seats
        if (!section.hasAvailableSeats()) {
            throw new IllegalStateException(
                    "Course section is full. Available seats: 0"
            );
        }


        // Get related course and session
        Course course = section.getCourse();
        Session session = course.getSession();
        String courseCode = course.getCourseCode();

        // Check if student not enrolled now
        Optional<Enrollment> existingCourseEnrollment =
                enrollmentRepo.findActiveEnrollmentByStudentAndCourse(
                        request.getStudentId(),
                        courseCode
                );

        if (existingCourseEnrollment.isPresent()) {
            Enrollment existing = existingCourseEnrollment.get();
            throw new IllegalArgumentException(
                    "Currently have enrolled in this course:" + existing.getSectionCode()
            );
        }

        // Validate prerequisites and CEFR requirements
        validateCourseRequirements(student, course);

        // Build enrollment entity
        Enrollment enrollment = new Enrollment();

        // Set relationships
        enrollment.setStudent(student);
        enrollment.setSection(section);

        // Set denormalized fields
        enrollment.setStudentNumber(student.getStudentNumber());
        enrollment.setStudentName(student.getFullName());
        enrollment.setStudentEmail(student.getEmail());
        enrollment.setCourseCode(course.getCourseCode());
        enrollment.setSectionCode(section.getSectionCode());
        enrollment.setSessionCode(session.getSessionCode());

        // Enrollment details
        enrollment.setHoursPerWeek(section.getHoursPerWeek());
        enrollment.setEnrolledTime(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        // Save enrollment
        Enrollment savedEnrollment = enrollmentRepo.save(enrollment);

        // Update section enrolled count
        section.enrollStudent();
        sectionRepo.save(section);

        // Update student stats
        student.enrollInCourse(section.getHoursPerWeek());
        studentRepo.save(student);

        return enrollmentMapper.toResponse(savedEnrollment);
    }

//    /**
//     * Admin batch enrollment
//     * Enroll multiple students into one CourseSection
//     */
//    @Transactional
//    public List<EnrollmentRes> batchEnrollStudents(
//            List<String> studentIds,
//            String sectionId
//    ) {
//        List<EnrollmentRes> results = new ArrayList<>();
//
//        for (String studentId : studentIds) {
//            try {
//                EnrollCourseReq req = new EnrollCourseReq();
//                req.setStudentId(studentId);
//                req.setCourseSectionId(sectionId);
//
//                EnrollmentRes result = enrollCourse(req);
//                results.add(result);
//
//            } catch (Exception e) {
//                // Log error and continue with next student
//                System.err.println("Failed to enroll student ID " + studentId +
//                        ": " + e.getMessage());
//            }
//        }
//
//        return results;
//    }

    /**
     * Drop an enrollment
     * Can be initiated by student or admin
     */
    @Transactional
    public EnrollmentRes dropCourse(DropCourseReq request, String operatorId) {
        // Find enrollment
        Enrollment enrollment = enrollmentRepo.findById(request.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment", request.getEnrollmentId()
                ));

        // Check if can drop
        if (!enrollment.canDrop()) {
            throw new IllegalStateException(
                    "Cannot drop enrollment with status: " + enrollment.getStatus()
            );
        }

        enrollment.drop(request.getDropReason(),operatorId);

        // Drop enrollment
        Enrollment droppedEnrollment = enrollmentRepo.save(enrollment);

        // Update section and student
        CourseSection section = enrollment.getSection();
        if (section != null) {
            section.dropStudent();
            sectionRepo.save(section);
        }

        Student student = enrollment.getStudent();
        if (student != null) {
            student.dropCourse(enrollment.getHoursPerWeek());
            studentRepo.save(student);
        }

        return enrollmentMapper.toResponse(droppedEnrollment);
    }

    /**
     * Complete enrollment with final grade
     */
    @Transactional
    public EnrollmentRes completeEnrollment(CompleteEnrollReq request) {
        Enrollment enrollment = enrollmentRepo.findById(request.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment", request.getEnrollmentId()
                ));

        if (!enrollment.isActive()) {
            throw new IllegalStateException(
                    "Can only complete active enrollments. Current status: " +
                            enrollment.getStatus()
            );
        }

        // Complete with grade
        enrollment.complete(request.getFinalGrade());

        Enrollment completedEnrollment = enrollmentRepo.save(enrollment);

        // Update student academic record
        Student student = enrollment.getStudent();
        if (student != null) {
            student.updateAcademicRecord(
                    enrollment.getFinalGrade()
            );
            studentRepo.save(student);
        }

        return enrollmentMapper.toResponse(completedEnrollment);
    }


    /**
     * Get enrollments list (simplified)
     */
    public List<EnrollmentList> getEnrollmentsList() {
        return enrollmentMapper.toListItems(enrollmentRepo.findAll());
    }



    /**
     * Get enrollment by ID
     */
    public EnrollmentRes getEnrollment(String enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment", enrollmentId
                ));
        return enrollmentMapper.toResponse(enrollment);
    }

    /**
     * Get enrollments by student
     */
    @Transactional(readOnly = true)
    public List<EnrollmentRes> getEnrollmentsByStudent(String studentId) {
        return enrollmentMapper.toResponseList(
                enrollmentRepo.findByStudentIdWithRelations(studentId)
        );
    }

    /**
     * Get active enrollments by student
     */
    @Transactional(readOnly = true)
    public List<EnrollmentRes> getActiveEnrollmentsByStudent(String studentId) {
        List<Enrollment> enrollments = enrollmentRepo.findByStudentIdWithRelations(studentId);
        return enrollmentMapper.toResponseList(
                enrollments.stream()
                        .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                        .toList()
        );
    }

    /**
     * Get completed enrollments by student
     */
    @Transactional(readOnly = true)
    public List<EnrollmentRes> getCompletedEnrollmentsByStudent(String studentId) {
        List<Enrollment> enrollments = enrollmentRepo.findByStudentIdWithRelations(studentId);
        return enrollmentMapper.toResponseList(
                enrollments.stream()
                        .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                        .toList()
        );
    }

    /**
     * Get all enrollments by section (for admin)
     */
    public List<EnrollmentRes> getAllEnrollmentsBySection(String sectionId) {
        // Validate section exists
        sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        // Get ALL enrollments (including DROPPED)
        List<Enrollment> enrollments = enrollmentRepo.findByCourseSectionIdWithRelations(sectionId);

        return enrollmentMapper.toResponseList(enrollments);
    }


    /**
     * Get enrollments by section (for Section Detail → People page)
     */
    public List<EnrollmentRes> getEnrollmentsBySection(String sectionId, String currentUserId) {
        // Check access
        CourseSection section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        // Instructor of this section OR enrolled student
        boolean isInstructor = section.getInstructor().getId().equals(currentUserId);
        boolean isEnrolledStudent = enrollmentRepo.existsByStudentAndSection(currentUserId, sectionId);

        if (!isInstructor && !isEnrolledStudent) {
            throw new IllegalStateException("Access Denied");
        }
        // Get enrollments
        List<Enrollment> enrollments = enrollmentRepo.findByCourseSectionIdWithRelations(sectionId);

        return enrollmentMapper.toResponseList(
                enrollments.stream()
                        .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED
                                || e.getStatus() == EnrollmentStatus.COMPLETED)
                        .toList()
        );
    }



//    /**
//     * Get enrollments for report export
//     */
//    public List<EnrollmentRes> getEnrollmentsForReport(String sessionCode) {
//        return enrollmentMapper.toResponseList(
//                enrollmentRepo.findBySessionCode(sessionCode)
//        );
//    }

    // ==== ADMIN REPORTING METHODS ====

    /**
     * Get session statistics
     */
    public long getEnrollmentCountBySession(String sessionCode) {
        return enrollmentRepo.countBySessionCode(sessionCode);
    }

    /**
     * Get all enrollments (detailed)
     */
    public List<EnrollmentRes> getAllEnrollments() {
        return enrollmentMapper.toResponseList(enrollmentRepo.findAll());
    }



    /**
     * Get enrollments by student and session
     */
    public List<EnrollmentRes> getEnrollmentsByStudentAndSession(
            String studentNumber,
            String sessionCode) {
        return enrollmentMapper.toResponseList(
                enrollmentRepo.findByStudentAndSession(studentNumber, sessionCode)
        );
    }



    /**
     * Get enrollments by status
     */
    public List<EnrollmentRes> getEnrollmentsByStatus(EnrollmentStatus status) {
        return enrollmentMapper.toResponseList(
                enrollmentRepo.findByStatus(status)
        );
    }


    /**
     * Validate course requirements (prerequisites and placement)
     */
    private void validateCourseRequirements(Student student, Course course) {
        if (!course.hasPrerequisites()) {
            return;
        }

        // Get all completed and passed courses by this student
        List<String> completedCourses = enrollmentRepo
                .findByStudentIdWithRelations(student.getId())
                .stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED
                        || e.getStatus() == EnrollmentStatus.ENROLLED)
                .map(Enrollment::getCourseCode)
                .toList();

        // Use Course's validation method
        if (!course.meetsPrerequisites(completedCourses, student.getPlacementLevel())) {
            throw new IllegalStateException("Prerequisites not met for " + course.getCourseCode());
        }
    }

}