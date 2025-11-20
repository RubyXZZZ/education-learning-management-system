package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.CourseSection;
import io.rubyxzzz.lms.backend.model.CourseSectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Course Section Repository
 */

@Repository
public interface SectionRepo extends JpaRepository<CourseSection, String> {

    List<CourseSection> findByStatus(CourseSectionStatus status);


    List<CourseSection> findBySessionCode(String sessionCode);

    @Query("SELECT DISTINCT cs FROM CourseSection cs " +
            "LEFT JOIN FETCH cs.course " +
            "LEFT JOIN FETCH cs.instructor " +
            "WHERE cs.course.id = :courseId")
    List<CourseSection> findByCourseId(@Param("courseId") String courseId);

    @Query("SELECT cs FROM CourseSection cs WHERE cs.course.session.id = :sessionId")
    List<CourseSection> findBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT DISTINCT s FROM CourseSection s " +
            "LEFT JOIN FETCH s.course c " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "LEFT JOIN FETCH c.session " +
            "LEFT JOIN FETCH s.instructor " +
            "WHERE s.instructor.id = :instructorId")
    List<CourseSection> findByInstructorIdWithRelations(@Param("instructorId") String instructorId);

    @Query("SELECT cs FROM CourseSection cs WHERE cs.enrolledCount >= cs.capacity")
    List<CourseSection> findFullSections();

    @Query("SELECT cs FROM CourseSection cs WHERE cs.enrolledCount < cs.minEnrollment")
    List<CourseSection> findUnderfullSections();

    @Query("SELECT s FROM CourseSection s " +
            "LEFT JOIN FETCH s.course c " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "LEFT JOIN FETCH c.session " +
            "LEFT JOIN FETCH s.instructor " +
            "WHERE s.id = :id")
    Optional<CourseSection> findByIdWithRelations(@Param("id") String id);



    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM CourseSection cs WHERE cs.course.id = :courseId AND cs.sectionCode = :sectionCode")
    boolean existsByCourseAndSectionCode(
            @Param("courseId") String courseId,
            @Param("sectionCode") String sectionCode
    );



    @Query("SELECT DISTINCT s FROM CourseSection s " +
            "LEFT JOIN FETCH s.course c " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "LEFT JOIN FETCH s.instructor " +
            "WHERE s.status = 'PUBLISHED' " +
            "AND s.enrollmentLocked = false " +
            "AND s.enrolledCount < s.capacity")
    List<CourseSection> findEnrollableSectionsWithRelations();



    @Query("""
    SELECT cs FROM CourseSection cs
    WHERE cs.instructor.id = :instructorId
      AND cs.course.session.id = :sessionId
""")
    List<CourseSection> findByInstructorAndSession(String instructorId, String sessionId);

    // for deleting course validation
    @Query("SELECT COUNT(s) FROM CourseSection s WHERE s.course.id = :courseId")
    long countByCourseId(@Param("courseId") String courseId);

}