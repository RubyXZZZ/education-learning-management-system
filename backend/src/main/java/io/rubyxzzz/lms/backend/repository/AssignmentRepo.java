package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Assignment;
import io.rubyxzzz.lms.backend.model.AssignmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepo extends JpaRepository<Assignment, String>{

    //Find assignments by course section
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.submissions " +
            "WHERE a.courseSection.id = :sectionId")
    List<Assignment> findByCourseSectionId(@Param("sectionId") String sectionId);

    //Find assignment by ID with relations loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.courseSection " +
            "LEFT JOIN FETCH a.submissions " +
            "WHERE a.id = :id")
    Optional<Assignment> findByIdWithRelations(@Param("id") String id);

    //Find published assignments by section (student view)
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.submissions " +
            "WHERE a.courseSection.id = :sectionId " +
            "AND a.isPublished = true " +
            "ORDER BY a.dueDate")
    List<Assignment> findPublishedBySection(@Param("sectionId") String sectionId);


    //Find assignments by type
    @Query("SELECT a FROM Assignment a " +
            "WHERE a.courseSection.id = :sectionId " +
            "AND a.assignmentType = :type")
    List<Assignment> findBySectionAndType(
            @Param("sectionId") String sectionId,
            @Param("type") AssignmentType type
    );

    //Find upcoming assignments (due soon)
    @Query("SELECT a FROM Assignment a " +
            "WHERE a.courseSection.id = :sectionId " +
            "AND a.isPublished = true " +
            "AND a.dueDate > :now " +
            "AND a.dueDate <= :until " +
            "ORDER BY a.dueDate")
    List<Assignment> findUpcomingAssignments(
            @Param("sectionId") String sectionId,
            @Param("now") LocalDateTime now,
            @Param("until") LocalDateTime until
    );

    //Find overdue assignments
    @Query("SELECT a FROM Assignment a " +
            "WHERE a.courseSection.id = :sectionId " +
            "AND a.isPublished = true " +
            "AND a.dueDate < :now " +
            "ORDER BY a.dueDate DESC")
    List<Assignment> findOverdueAssignments(
            @Param("sectionId") String sectionId,
            @Param("now") LocalDateTime now
    );

    //Count assignments in section
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.courseSection.id = :sectionId")
    long countByCourseSectionId(@Param("sectionId") String sectionId);

}
