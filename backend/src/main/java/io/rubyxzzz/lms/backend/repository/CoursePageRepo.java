package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.CoursePage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoursePageRepo extends JpaRepository<CoursePage, String> {

    //Find pages by course section(ordered)
    @Query("SELECT p FROM CoursePage p " +
            "LEFT JOIN FETCH p.module " +
            "WHERE p.courseSection.id = :sectionId " +
            "ORDER BY p.module.orderNum, p.orderNum, p.createdAt")
    List<CoursePage> findByCourseSectionId(@Param("sectionId") String sectionId);

    //Find page by ID with relations loaded
    @Query("SELECT p FROM CoursePage p " +
            "LEFT JOIN FETCH p.courseSection " +
            "LEFT JOIN FETCH p.module " +
            "WHERE p.id = :id")
    Optional<CoursePage> findByIdWithRelations(@Param("id") String id);

    //Find pages by module，ordered
    @Query("SELECT p FROM CoursePage p " +
            "WHERE p.module.id = :moduleId " +
            "ORDER BY p.orderNum, p.createdAt")
    List<CoursePage> findByModule(@Param("moduleId") String moduleId);

    //Find published pages by section (student view)
    @Query("SELECT p FROM CoursePage p " +
            "LEFT JOIN FETCH p.module " +
            "WHERE p.courseSection.id = :sectionId " +
            "AND p.isPublished = true " +
            "ORDER BY p.module.orderNum, p.orderNum, p.createdAt")
    List<CoursePage> findPublishedBySection(@Param("sectionId") String sectionId);

    // find published pages by module (student view)
    @Query("SELECT p FROM CoursePage p " +
            "LEFT JOIN FETCH p.module " +
            "WHERE p.module.id = :moduleId " +
            "AND p.isPublished = true " +
            "ORDER BY p.orderNum, p.createdAt")
    List<CoursePage> findPublishedByModule(@Param("moduleId") String moduleId);

    //Count pages in section
//    @Query("SELECT COUNT(p) FROM CoursePage p WHERE p.courseSection.id = :sectionId")
//    long countByCourseSectionId(@Param("sectionId") String sectionId);

    //Count pages in module
    @Query("SELECT COUNT(p) FROM CoursePage p WHERE p.module.id = :moduleId")
    long countByModuleId(@Param("moduleId") String moduleId);

    @Query("SELECT COALESCE(MAX(p.orderNum), 0) FROM CoursePage p " +
            "WHERE p.module.id = :moduleId")
    Integer getMaxOrderNumByModule(@Param("moduleId") String moduleId);
}
