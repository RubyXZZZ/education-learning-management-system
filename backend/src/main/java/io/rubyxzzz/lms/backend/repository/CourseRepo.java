package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface CourseRepo extends JpaRepository<Course, String> {

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.session " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "LEFT JOIN FETCH c.sections " +
            "ORDER BY c.session.sessionCode DESC, c.courseCode ASC")
    List<Course> findAllWithRelations();


    @Query("SELECT c FROM Course c " +
            "LEFT JOIN FETCH c.session " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "LEFT JOIN FETCH c.sections " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithRelations(@Param("id") String id);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.session " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "WHERE c.session.id = :sessionId")
    List<Course> findBySessionIdWithRelations(@Param("sessionId") String sessionId);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.session " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "WHERE c.session.sessionCode = :sessionCode")
    List<Course> findBySessionCodeWithRelations(@Param("sessionCode") String sessionCode);


    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.session " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "WHERE c.isActive = true")
    List<Course> findActiveCoursesWithRelations();



    // TODO：used for copy courses between sessions
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.sections WHERE c.id = :id")
    Optional<Course> findByIdWithSections(String id);
}