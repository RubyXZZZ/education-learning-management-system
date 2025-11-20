package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base Entity - Abstract parent class for all domain entities
 * holds common fields and methods
 * UUID, createdAt, updatedAt ...
 */

@Data
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    /**
     * Primary Key - UUID
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;  // system id

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", length = 36)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 36)
    private String updatedBy;

//    /**
//     * Called automatically before INSERT by JPA
//     */
//    @PrePersist
//    protected void onCreateJpa() {
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
//    }
//    @PreUpdate
//    protected void onUpdateJpa() {
//        this.updatedAt = LocalDateTime.now();
//    }
//
//
//
//    public void onCreate() {
//        if (this.id == null) {
//            this.id = UUID.randomUUID().toString();
//        }
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public void onUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
//
//
//    public void onCreate(String creatorId) {
//        if (this.id == null) {
//            this.id = UUID.randomUUID().toString();
//        }
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
//        this.createdBy = creatorId != null ? creatorId : "SYSTEM";
//        this.updatedBy = this.createdBy;
//    }
//
//
//    // Life cycle method - update
//    public void onUpdate(String updaterId) {
//        this.updatedAt = LocalDateTime.now();
//        this.updatedBy = updaterId != null ? updaterId : "SYSTEM";
//    }

    // check if is new = not save to db yet
    public boolean isNew(){
        return this.id == null;
    }

    // check if is persisted
    public boolean isPersisted(){
        return this.id != null;
    }

    // check who created
    public boolean isSystemCreated() {
        return "SYSTEM".equals(this.createdBy);
    }

    public boolean isCreatedBy(String userId) {
        return this.createdBy != null && this.createdBy.equals(userId);
    }

}
