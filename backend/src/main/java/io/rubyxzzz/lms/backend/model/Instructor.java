package io.rubyxzzz.lms.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors", indexes = {
        @Index(name = "idx_employee_number", columnList = "employee_number", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_department", columnList = "department"),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Instructor extends User {

    @Column(name = "employee_number", unique = true, nullable = false, length = 20)
    private String employeeNumber;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "office_hours", length = 100)
    private String officeHours;

    @Column(name = "teaching_counts", nullable = false)
    private Integer teachingCounts = 0;

    @OneToMany(
            mappedBy = "instructor",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CourseSection> sections = new ArrayList<>();

    @Override
    public String getUserNumber() {
        return this.employeeNumber;
    }

    @Override
    public UserRole getUserRole() {
        return UserRole.INSTRUCTOR;
    }

    @Override
    public boolean canEnrollCourse() {
        return false;
    }

    @Override
    public boolean canTeachCourse() {
        return true;
    }

//    // check if instructor can teach more courses
//    public boolean canTeachMore() {
//        return this.teachingCounts < BusinessConstants.MAX_TEACHING_LOAD;
//    }

    public void assignedCourse() {

        if (teachingCounts == null) {
            teachingCounts = 0;
        }
        this.teachingCounts++;
    }

    public void removeCourse() {
        this.teachingCounts--;
    }
}


