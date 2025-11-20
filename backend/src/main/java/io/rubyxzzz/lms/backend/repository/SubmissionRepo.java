package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Submission;
import io.rubyxzzz.lms.backend.model.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, String> {
    // find submission by ID with relations loaded
    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.assignment " +
            "LEFT JOIN FETCH s.student " +
            "WHERE s.id = :id")
    Optional<Submission> findByIdWithRelations(@Param("id") String id);

    // find latest submission by assignment and student
    @Query("SELECT s FROM Submission s " +
            "WHERE s.assignment.id = :assignmentId " +
            "AND s.student.id = :studentId " +
            "AND s.isLatest = true")
    Optional<Submission> findLatestByAssignmentAndStudent(
            @Param("assignmentId") String assignmentId,
            @Param("studentId") String studentId
    );

    // find all submission history(all attempts)
    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.student " +
            "WHERE s.assignment.id = :assignmentId " +
            "AND s.student.id = :studentId " +
            "ORDER BY s.attemptNumber DESC")
    List<Submission> findAllAttemptsByAssignmentAndStudent(
            @Param("assignmentId") String assignmentId,
            @Param("studentId") String studentId
    );

    // find submissions by assignment(all students)
    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.student " +
            "WHERE s.assignment.id = :assignmentId " +
            "AND s.isLatest = true")
    List<Submission> findLatestByAssignment(@Param("assignmentId") String assignmentId);

    // find submissions by student
    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.assignment " +
            "WHERE s.student.id = :studentId " +
            "AND s.isLatest = true")
    List<Submission> findLatestByStudent(@Param("studentId") String studentId);

    // find ungraded submissions by assignment
    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.student " +
            "WHERE s.assignment.id = :assignmentId " +
            "AND s.isLatest = true " +
            "AND s.status IN ('SUBMITTED', 'LATE')")
    List<Submission> findUngradedByAssignment(@Param("assignmentId") String assignmentId);

    // find graded submissions by assignment
    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.student " +
            "WHERE s.assignment.id = :assignmentId " +
            "AND s.isLatest = true " +
            "AND s.status = 'GRADED'")
    List<Submission> findGradedByAssignment(@Param("assignmentId") String assignmentId);

    // count submissions by assignment
    @Query("SELECT COUNT(s) FROM Submission s " +
            "WHERE s.assignment.id = :assignmentId " +
            "AND s.isLatest = true")
    long countByAssignmentId(@Param("assignmentId") String assignmentId);
}
