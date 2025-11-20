package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepo extends JpaRepository<Module, String> {
    // find modules by course section, ordered by orderNum
    @Query("SELECT m FROM Module m WHERE m.courseSection.id = :sectionId ORDER BY m.orderNum")
    List<Module> findAllBySection(String sectionId);

    // find module by ID with section loaded
    @Query("SELECT m FROM Module m " +
            "LEFT JOIN FETCH m.courseSection " +
            "WHERE m.id = :id")
    Optional<Module> findByIdWithSection(@Param("id") String id);

    // find published modules by section (visible to students)
    @Query("SELECT m FROM Module m " +
            "WHERE m.courseSection.id = :sectionId " +
            "AND m.isPublished = true " +
            "ORDER BY m.orderNum")
    List<Module> findPublishedBySection(@Param("sectionId") String sectionId);

    // count modules in a course section
    @Query("SELECT COUNT(m) FROM Module m WHERE m.courseSection.id = :sectionId")
    long countByCourseSectionId(@Param("sectionId") String sectionId);

    // get max orderNum in a course section
    @Query("SELECT COALESCE(MAX(m.orderNum), 0) FROM Module m WHERE m.courseSection.id = :sectionId")
    Integer getMaxOrderNumBySection(@Param("sectionId") String sectionId);

}
