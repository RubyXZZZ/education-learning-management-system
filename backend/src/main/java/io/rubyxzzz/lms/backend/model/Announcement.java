package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Announcement Entity
 * Course announcements that notify all enrolled students
 *
 * Features:
 * - Sends notifications to all students (email/in-app)
 */
@Entity
@Table(name = "announcements", indexes = {
        @Index(name = "idx_course_section", columnList = "course_section_id"),
        @Index(name = "idx_posted_date", columnList = "posted_at"),
        @Index(name = "idx_pinned", columnList = "is_pinned")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity {

    /**
     * Course section this announcement belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_section_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_announcement_section")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseSection courseSection;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "posted_at", nullable = false)
    private LocalDateTime postedAt;

    // TODO: EMAIL SYSTEM ???
    @Column(name = "send_email")
    private Boolean sendEmail = true;

    // TODO: NOTIFICATION SYSTEM ???
    @Column(name = "send_notification")
    private Boolean sendNotification = true;

    // TODO:MARK AS READ/UNREAD???
}
