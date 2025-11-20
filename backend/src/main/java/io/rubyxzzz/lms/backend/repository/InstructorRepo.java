package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Instructor;
import io.rubyxzzz.lms.backend.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface InstructorRepo extends JpaRepository<Instructor, String> {

    @Query("SELECT DISTINCT i FROM Instructor i " +
            "LEFT JOIN FETCH i.sections s " +
            "LEFT JOIN FETCH s.course c " +
            "LEFT JOIN FETCH c.prerequisiteCourses " +
            "WHERE i.id = :id")
    Optional<Instructor> findByIdWithRelations(@Param("id") String id);

    Optional<Instructor> findByFirebaseUid(String firebaseUid);

    List<Instructor> findByStatus(UserStatus status);

    Optional<Instructor> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Instructor> findByEmployeeNumber(String employeeNumber);

    boolean existsByEmployeeNumber(String employeeNumber);

    List<Instructor> findByDepartment(String department);

}
