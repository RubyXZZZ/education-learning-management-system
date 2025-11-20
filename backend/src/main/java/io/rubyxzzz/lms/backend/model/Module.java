package io.rubyxzzz.lms.backend.model;


import jakarta.persistence.*;
import lombok.*;

/**
 * Module entity
 * Optional content organization within a course section
 */
@Entity
@Table(name = "modules", indexes = {
        @Index(name = "idx_course_section", columnList = "course_section_id"),
        @Index(name = "idx_order", columnList = "order_num")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Module extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_section_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_module_section")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseSection courseSection;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Display order (1, 2, 3...)
     */
    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "is_published")
    private Boolean isPublished = false;



}
