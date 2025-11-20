package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Admin;
import io.rubyxzzz.lms.backend.model.Instructor;
import io.rubyxzzz.lms.backend.model.Student;
import io.rubyxzzz.lms.backend.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface AdminRepo extends JpaRepository<Admin, String> {
    Optional<Admin> findByFirebaseUid(String firebaseUid);

    Optional<Admin> findByEmail(String email) ;

    boolean existsByEmail(String email);

    Optional<Admin> findByEmployeeNumber(String employeeNumber);

    boolean existsByEmployeeNumber(String employeeNumber);

    List<Admin> findByDepartment(String department);

    List<Admin> findByStatus(UserStatus status);


    @Query("SELECT a FROM Admin a WHERE a.isSuperAdmin = true")
    List<Admin> findSuperAdmins();

}
