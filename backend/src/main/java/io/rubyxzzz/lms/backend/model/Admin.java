package io.rubyxzzz.lms.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admins", indexes = {
        @Index(name = "idx_employee_number", columnList = "employee_number", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_is_super_admin", columnList = "is_super_admin"),
        @Index(name = "idx_department", columnList = "department"),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    @Column(name = "employee_number", unique = true, nullable = false, length = 20)
    private String employeeNumber;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "office_hours", length = 100)
    private String officeHours;

    @JsonProperty("isSuperAdmin")
    @Column(name = "is_super_admin", nullable = false)
    private Boolean isSuperAdmin = false;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(
//            name = "admin_department_scope",
//            joinColumns = @JoinColumn(name = "admin_id")
//    )
//    @Column(name = "department")
//    private Set<String> deptScope = new HashSet<>();

    @Override
    public String getUserNumber() {
        return employeeNumber;
    }

    @Override
    public UserRole getUserRole() {
        return UserRole.ADMIN;
    }

    @Override
    public boolean canEnrollCourse() {
        return false;
    }

    @Override
    public boolean canTeachCourse() {
        return false;
    }

    // all permissions admin, can create other admins
    public boolean isSuperAdmin() {
        return this.isSuperAdmin;
    }


    public void promoteToSuperAdmin() {
        this.isSuperAdmin = true;
    }

    public void demoteFromSuperAdmin() {
        this.isSuperAdmin = false;
    }

}
