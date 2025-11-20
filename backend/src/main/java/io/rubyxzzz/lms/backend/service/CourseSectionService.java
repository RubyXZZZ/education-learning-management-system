package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.listItem.CourseSectionList;
import io.rubyxzzz.lms.backend.dto.request.CreateSectionReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateSectionReq;
import io.rubyxzzz.lms.backend.dto.response.SectionRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.SectionMapper;
import io.rubyxzzz.lms.backend.model.*;
import io.rubyxzzz.lms.backend.repository.CourseRepo;
import io.rubyxzzz.lms.backend.repository.InstructorRepo;
import io.rubyxzzz.lms.backend.repository.SectionRepo;
import io.rubyxzzz.lms.backend.repository.SessionRepo;
import io.rubyxzzz.lms.backend.util.UpdateUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Course Section Service
 * Business logic for course section management
 *
 * Responsibilities:
 * - Section CRUD operations
 * - Enrollment management
 * - Instructor assignment
 * - Status transitions
 */
@Service
@RequiredArgsConstructor
public class CourseSectionService {

    private final SectionRepo sectionRepo;
    private final CourseRepo courseRepo;
    private final SessionRepo sessionRepo;
    private final InstructorRepo instructorRepo;
    private final SectionMapper sectionMapper;

    /**
     * Create a new course section
     */
    @Transactional
    public SectionRes createSection(CreateSectionReq request) {
        // Validate course template exists
        Course course = courseRepo.findByIdWithRelations(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course", request.getCourseId()
                ));

        Session session = course.getSession();

        // Check section uniqueness
        if (sectionRepo.existsByCourseAndSectionCode(
                request.getCourseId(),
                request.getSectionCode())) {
            throw new IllegalArgumentException(
                    "Section " + request.getSectionCode() +
                            " already exists for this course"
            );
        }


        // Validate instructor
        Instructor instructor = instructorRepo.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor", request.getInstructorId()
                ));


        // Build section entity
        CourseSection section = new CourseSection();

        BeanUtils.copyProperties(request, section);

        // Set associations
        section.setCourse(course);
        section.setInstructor(instructor);

        // Denormalize fields
        section.setCourseCode(course.getCourseCode());
        section.setCourseName(course.getCourseName());
        section.setSessionCode(course.getSessionCode());
        section.setHoursPerWeek(course.getHoursPerWeek());
        section.setInstructorName(instructor.getFullName());
        section.setInstructorEmail(instructor.getEmail());
        String schedule = generateSchedule(
                request.getDaysOfWeek(),
                request.getStartTime(),
                request.getEndTime()
        );
        section.setSchedule(schedule);



        // Save section
        CourseSection savedSection = sectionRepo.save(section);

        // Update instructor teaching count
        instructor.assignedCourse();
        instructorRepo.save(instructor);

        return sectionMapper.toResponse(savedSection);
    }

    /**
     * Update course section
     */
    @Transactional
    public SectionRes updateSection(String sectionId, UpdateSectionReq request) {
        CourseSection section = sectionRepo.findByIdWithRelations(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseSection", sectionId));

        // Update fields (only non-null values)
        UpdateUtil.copyNonNullProperties(request, section);

        // Regenerate schedule if any schedule field changed
        if (request.getDaysOfWeek() != null ||
                request.getStartTime() != null ||
                request.getEndTime() != null) {

            String newSchedule = generateSchedule(
                    section.getDaysOfWeek(),
                    section.getStartTime(),
                    section.getEndTime()
            );
            section.setSchedule(newSchedule);
        }



        // Handle instructor change
        if (request.getInstructorId() != null) {
            Instructor currentInstructor = section.getInstructor();
            if (currentInstructor == null ||
                    !request.getInstructorId().equals(currentInstructor.getId())) {
                changeInstructor(section, request.getInstructorId());
            }
        }


        CourseSection updatedSection = sectionRepo.save(section);
        return sectionMapper.toResponse(updatedSection);
    }

    /**
     * Generate schedule display string
     */
    private String generateSchedule(String daysOfWeek, LocalTime startTime, LocalTime endTime) {
        if (daysOfWeek == null || daysOfWeek.isBlank() ||
                startTime == null || endTime == null) {
            return null;
        }

        // Abbreviate days: "Monday,Wednesday" -> "Mon/Wed"
        String[] days = daysOfWeek.split(",");
        String abbreviatedDays = Arrays.stream(days)
                .map(String::trim)
                .map(this::abbreviateDay)
                .collect(Collectors.joining("/"));

        // Format times: "10:00-11:30"
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeRange = String.format("%s-%s",
                startTime.format(timeFormatter),
                endTime.format(timeFormatter)
        );

        return String.format("%s %s", abbreviatedDays, timeRange);
    }

    /**
     * Abbreviate day name
     */
    private String abbreviateDay(String day) {
        switch (day.trim()) {
            case "Monday": return "Mon";
            case "Tuesday": return "Tue";
            case "Wednesday": return "Wed";
            case "Thursday": return "Thu";
            case "Friday": return "Fri";
            case "Saturday": return "Sat";
            case "Sunday": return "Sun";
            default: return day;
        }
    }

    /**
     * Get section by UUID
     */
    public SectionRes getSection(String sectionId) {
        CourseSection section = sectionRepo.findByIdWithRelations(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CourseSection", sectionId
                ));
        return sectionMapper.toResponse(section);
    }



    /**
     * Get all sections
     */
    public List<SectionRes> getAllSections() {
        return sectionMapper.toResponseList(sectionRepo.findAll());
    }

    /**
     * Get sections list (simplified)
     */
    public List<CourseSectionList> getSectionsByCourse(String courseId) {
        return sectionMapper.toListItems(sectionRepo.findByCourseId(courseId));
    }

    /**
     * Get sections by session
     */
    public List<SectionRes> getSectionsBySession(String sessionId) {
        return sectionMapper.toResponseList(
                sectionRepo.findBySessionId(sessionId)
        );
    }



    /**
     * Get sections by instructor
     */
    public List<SectionRes> getSectionsByInstructor(String instructorId) {
        return sectionMapper.toResponseList(
                sectionRepo.findByInstructorIdWithRelations(instructorId)
        );
    }

    /**
     * Get active sections by instructor (PUBLISHED status only)
     */
    public List<SectionRes> getActiveSectionsByInstructor(String instructorId) {
        List<CourseSection> sections = sectionRepo.findByInstructorIdWithRelations(instructorId);

        // Filter PUBLISHED sections only
        List<CourseSection> activeSections = sections.stream()
                .filter(s -> s.getStatus() == CourseSectionStatus.PUBLISHED)
                .collect(Collectors.toList());

        return sectionMapper.toResponseList(activeSections);
    }



    /**
     * Get open sections (available for enrollment)
     */
    public List<SectionRes> getEnrollableSections() {
        List<CourseSection> sections = sectionRepo.findEnrollableSectionsWithRelations();
        return sectionMapper.toResponseList(sections);
    }

//    /**
//     * Get open sections by session
//     */
//    public List<CourseSectionList> getEnrollableSectionsBySession(String sessionId) {
//        return sectionMapper.toListItems(
//                sectionRepo.findEnrollableSectionsBySession(sessionId)
//        );
//    }

    /**
     * Get sections by status
     */
    public List<SectionRes> getSectionsByStatus(CourseSectionStatus status){
        return sectionMapper.toResponseList(
                sectionRepo.findByStatus(status)
        );
    };


    /**
     * Get sections by instructor and session
     */

    public List<SectionRes> getSectionsByInstructorAndSession(
            String instructorId,
            String sessionId
    ){
        return sectionMapper.toResponseList(
                sectionRepo.findByInstructorAndSession(instructorId, sessionId)
        );
    };

    /**
     * Get full sections (capacity reached)
     */
    public List<SectionRes> getFullSections() {
        return sectionMapper.toResponseList(
                sectionRepo.findFullSections()
        );
    }



    /**
     * Get underfull sections (below minimum enrollment)
     */
    public List<SectionRes> getUnderfullSections() {
        return sectionMapper.toResponseList(
                sectionRepo.findUnderfullSections()
        );
    }

    /**
     * Publish section (make it open for enrollment)
     */
    @Transactional
    public SectionRes publishSection(String sectionId) {
        CourseSection section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CourseSection", sectionId
                ));

        if (section.getStatus() != CourseSectionStatus.DRAFT) {
            throw new IllegalStateException("Can only publish DRAFT sections");
        }

        // Validate section has required info
        if (section.getInstructor() == null) {
            throw new IllegalStateException(
                    "Section must have an assigned instructor before publishing"
            );
        }
        if (section.getSchedule() == null) {
            throw new IllegalStateException(
                    "Section must have a schedule before publishing"
            );
        }

        section.setStatus(CourseSectionStatus.PUBLISHED);

        CourseSection updatedSection = sectionRepo.save(section);
        return sectionMapper.toResponse(updatedSection);
    }



//    /**
//     * Complete a section
//     */
//    @Transactional
//    public SectionRes completeSection(String sectionId) {
//        CourseSection section = sectionRepo.findById(sectionId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "CourseSection", sectionId
//                ));
//
//        if (section.getStatus() != CourseSectionStatus.IN_PROGRESS) {
//            throw new IllegalStateException(
//                    "Can only complete sections that are IN_PROGRESS"
//            );
//        }
//
//        section.setStatus(CourseSectionStatus.COMPLETED);
//
//        CourseSection updatedSection = sectionRepo.save(section);
//        return sectionMapper.toResponse(updatedSection);
//    }

    /**
     * Cancel a section (minimum enrollment not met)
     */
    @Transactional
    public SectionRes cancelSection(String sectionId) {
        CourseSection section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CourseSection", sectionId
                ));

        if (section.getStatus() == CourseSectionStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Cannot cancel completed sections"
            );
        }

        section.setStatus(CourseSectionStatus.CANCELLED);

        CourseSection updatedSection = sectionRepo.save(section);
        return sectionMapper.toResponse(updatedSection);
    }

    /**
     * Delete section (hard delete)
     */
    @Transactional
    public void deleteSection(String sectionId) {
        CourseSection section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CourseSection", sectionId
                ));

        // Cannot delete sections that have started or have enrollments
        if (section.getStatus() != CourseSectionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Can only delete DRAFT sections. "
            );
        }

        if (section.getEnrolledCount() > 0) {
            throw new IllegalStateException(
                    "Cannot delete section with active enrollments"
            );
        }

        // Release instructor
        Instructor instructor = section.getInstructor();
        if (instructor != null) {
            instructor.removeCourse();
            instructorRepo.save(instructor);
        }

        sectionRepo.delete(section);
    }

    /**
     * Change section instructor
     */
    private void changeInstructor(CourseSection section, String newInstructorId) {
        // Remove from old instructor
        Instructor oldInstructor = section.getInstructor();
        if (oldInstructor != null) {
            oldInstructor.removeCourse();
            instructorRepo.save(oldInstructor);
        }

        // Assign to new instructor
        Instructor newInstructor = instructorRepo.findById(newInstructorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor", newInstructorId
                ));


        newInstructor.assignedCourse();

        instructorRepo.save(newInstructor);

        section.setInstructor(newInstructor);
        section.setInstructorName(newInstructor.getFullName());
        section.setInstructorEmail(newInstructor.getEmail());
    }
}
