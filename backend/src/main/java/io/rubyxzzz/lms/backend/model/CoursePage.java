package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Page Entity
 * Universal content container for rich text with embedded files/links
 *
 * Can be used for:
 * - module item
 * - Announcements
 * - Any rich text content
 *
 * The body field stores HTML that can contain:
 * - Formatted text
 * - Embedded images
 * - File links
 * - External links
 */

@Entity
@Table(name = "course_pages", indexes = {
        @Index(name = "idx_course_section", columnList = "course_section_id"),
        @Index(name = "idx_module", columnList = "module_id"),
        @Index(name = "idx_published", columnList = "is_published"),
        @Index(name = "idx_pinned", columnList = "is_pinned")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoursePage extends BaseEntity {

    //Must belong to a course section
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_section_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_page_section")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseSection courseSection;

    // MVP vision: must connect module
    // Can be null (global pages) in the future
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "module_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_page_module")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Module module;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    //Rich text content (HTML format)
    @Column(name = "body", columnDefinition = "LONGTEXT")
    private String body;

    @Column(name = "order_num")
    private Integer orderNum; // For ordering within module

    @Column(name = "is_published")
    private Boolean isPublished = false;

    public boolean isVisibleToStudent() {
        if (!Boolean.TRUE.equals(isPublished)) {
            return false;
        }

        if (!Boolean.TRUE.equals(module.getIsPublished())) {
            return false;
        }

        return true;
    }


}

