package io.rubyxzzz.lms.backend.model;

import io.rubyxzzz.lms.backend.security.RolePermissionMapping;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;

import java.util.Set;

/**
 * Abstract User Base Class
 *
 * - Defines common properties and behaviors for all users
 * - Cannot be instantiated directly
 * - Subclasses: Student, Instructor, Admin
 */

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@MappedSuperclass
public abstract class User extends BaseEntity {

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

//    @Column(name = "password", length = 255)
//    private String password;  // Hashed

    @Column(name = "firebase_uid", unique = true, nullable = false, length = 128)
    private String firebaseUid;

    @Column(name = "user_avatar", length = 255)
    private String userAvatar = null; // URL to avatar image

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;


    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;



    //Get user business number (visible to users)
    public abstract String getUserNumber();
    public abstract UserRole getUserRole();
    public abstract boolean canEnrollCourse();
    public abstract boolean canTeachCourse();


    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    @Transient
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

}
