package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Enrollment;
import io.rubyxzzz.lms.backend.model.EnrollmentMode;
import io.rubyxzzz.lms.backend.model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollment, String> {



    // used to search single student's all enrollments
    @Query("SELECT e FROM Enrollment e " +
            "LEFT JOIN FETCH e.student " +
            "LEFT JOIN FETCH e.section s " +
            "WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentIdWithRelations(@Param("studentId") String studentId);

//    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
//    List<Enrollment> findByStudentIdAndStatus(String studentId, EnrollmentStatus status);



    @Query("SELECT e FROM Enrollment e WHERE e.section.id = :sectionId")
    List<Enrollment> findByCourseSectionId(@Param("sectionId") String sectionId);

    // for section detail page to load all students' enrollments
    @Query("SELECT e FROM Enrollment e " +
            "LEFT JOIN FETCH e.student " +
            "LEFT JOIN FETCH e.section " +
            "WHERE e.section.id = :sectionId")
    List<Enrollment> findByCourseSectionIdWithRelations(@Param("sectionId") String sectionId);

    // for check the student's full enrollment history in a course
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.section.id = :sectionId")
    List<Enrollment> findByStudentAndCourseSection(
            @Param("studentId") String studentId,
            @Param("sectionId") String sectionId
    );


    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e WHERE e.student.id = :studentId AND e.section.id = :sectionId")
    boolean existsByStudentAndSection(String studentId, String sectionId);


    // for enroll operation validation and querying
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") String studentId);

    // for validating if student is already enrolled in the course(different sections)
    @Query("SELECT e FROM Enrollment e " +
            "WHERE e.student.id = :studentId " +
            "AND e.courseCode = :courseCode " +
            "AND e.status = 'ENROLLED'")
    Optional<Enrollment> findActiveEnrollmentByStudentAndCourse(
            @Param("studentId") String studentId,
            @Param("courseCode") String courseCode
    );

    @Query("SELECT e FROM Enrollment e WHERE e.studentNumber = :studentNumber AND e.sessionCode = :sessionCode")
    List<Enrollment> findByStudentAndSession(
            @Param("studentNumber") String studentNumber,
            @Param("sessionCode") String sessionCode
    );


    // == Used for reporting and analytics ===
    List<Enrollment> findByStatus(EnrollmentStatus status);


    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.sessionCode = :sessionCode")
    long countBySessionCode(@Param("sessionCode") String sessionCode);


    List<Enrollment> findBySessionCode(String sessionCode); // Used for exporting enrollments by session

    @Query("SELECT e FROM Enrollment e WHERE e.sessionCode = :sessionCode AND e.status = :status")
    List<Enrollment> findBySessionCodeAndStatus(@Param("sessionCode") String sessionCode, @Param("status") EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.section.id = :sectionId AND e.status = 'ENROLLED'")
    List<Enrollment> findActiveBySectionId(String sectionId);


    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.status = :status")
    long countByStatus(EnrollmentStatus status);


    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId")
    long countByStudentId(@Param("studentId") String studentId);
}

