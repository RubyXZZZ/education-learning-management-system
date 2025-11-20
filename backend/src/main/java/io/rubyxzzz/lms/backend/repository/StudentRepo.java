package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Student;
import io.rubyxzzz.lms.backend.model.StudentType;
import io.rubyxzzz.lms.backend.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface StudentRepo extends JpaRepository<Student, String> {
    Optional<Student> findByFirebaseUid(String firebaseUid);

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Student> findByStudentNumber(String studentNumber);

    boolean existsByStudentNumber(String studentNumber);

    List<Student> findByStatus(UserStatus status);

    List<Student> findByStudentType(StudentType studentType);

    List<Student> findByPlacementLevel(Integer placementLevel);

    @Query("SELECT DISTINCT s FROM Student s " +
            "LEFT JOIN FETCH s.enrollments e " +
            "LEFT JOIN FETCH e.section " +
            "WHERE s.id = :id")
    Optional<Student> findByIdWithRelations(@Param("id") String id);

    @Query("SELECT s.studentNumber FROM Student s WHERE s.studentNumber LIKE CONCAT('S', :year, '%') ORDER BY s.studentNumber DESC LIMIT 1")
    Optional<String> findLatestStudentNumberByYear(@Param("year") int year);
}