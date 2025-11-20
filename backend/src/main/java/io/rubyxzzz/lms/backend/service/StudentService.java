package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.listItem.StudentList;
import io.rubyxzzz.lms.backend.dto.request.CreateStudentReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateProfileReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateStudentReq;
import io.rubyxzzz.lms.backend.dto.response.StudentRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.StudentMapper;
import io.rubyxzzz.lms.backend.model.*;
import io.rubyxzzz.lms.backend.repository.StudentRepo;
import io.rubyxzzz.lms.backend.util.IdGenerator;
import io.rubyxzzz.lms.backend.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepo studentRepo;
    private final StudentMapper studentMapper;
    private final IdGenerator idGenerator;
    private final UserMgmtService userMgmtService;

    public StudentRes createStudent(CreateStudentReq request) {
        if (studentRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + request.getEmail()
            );
        }

        String firebaseUid = null;

        try {
            // 1. Create Firebase user first to get firebaseUid
            firebaseUid = userMgmtService.createFirebaseUser(
                    request.getEmail(),
                    request.getFirstName() + " " + request.getLastName()
            );

            // 2. Create student entity
            Student student = new Student();

            student.setStudentNumber(idGenerator.generateStudentNumber());
            student.setFirebaseUid(firebaseUid);

            BeanUtils.copyProperties(request, student);


            // 3. Save to database
            Student savedStudent = studentRepo.save(student);
            return studentMapper.toResponse(savedStudent);

        } catch (Exception e) {
            // Rollback: delete Firebase user if database save failed
            if (firebaseUid != null) {
                try {
                    userMgmtService.deleteFirebaseUser(firebaseUid);
                    log.warn("Rolled back Firebase user creation: {}", firebaseUid);
                } catch (Exception rollbackEx) {
                    log.error("Failed to rollback Firebase user: {}", rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Failed to create student: " + e.getMessage(), e);
        }
    }

    // use for updating own profile
    @Transactional
    public StudentRes updateStudentProfile(String studentId, UpdateProfileReq request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        // Update fields (only non-null)
        UpdateUtil.copyNonNullProperties(request, student);

        Student updated = studentRepo.save(student);
        return studentMapper.toResponse(updated);
    }

    @Transactional
    public StudentRes updateStudent(String studentId, UpdateStudentReq request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        UpdateUtil.copyNonNullProperties(request, student);

        Student updatedStudent = studentRepo.save(student);
        return studentMapper.toResponse(updatedStudent);
    }


    /**
     * Get student basic info (no enrollments)
     */
    public StudentRes getStudent(String studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        return studentMapper.toResponse(student);
    }

    /**
     * Get student with all enrollments
     * Returns all historical enrollments, frontend filters as needed
     */
    @Transactional(readOnly = true)
    public StudentRes getStudentWithEnrollments(String studentId) {
        Student student = studentRepo.findByIdWithRelations(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        return studentMapper.toResponseWithEnrollments(student);
    }

    public StudentRes getStudentByStudentNumber(String studentNumber) {
        Student student = studentRepo.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "studentNumber", studentNumber
                ));
        return studentMapper.toResponse(student);
    }

    public List<StudentRes> getAllStudents() {
        return studentMapper.toResponseList(studentRepo.findAll());
    }

    public List<StudentList> getStudentsList() {
        return studentMapper.toListItems(studentRepo.findAll());
    }

    public List<StudentRes> getStudentsByStatus(UserStatus status) {
        return studentMapper.toResponseList(
                studentRepo.findByStatus(status)
        );
    }

    public List<StudentRes> getStudentsByType(StudentType studentType) {
        return studentMapper.toResponseList(
                studentRepo.findByStudentType(studentType)
        );
    }

    public List<StudentRes> getStudentsByPlacementLevel(Integer placementLevel) {
        return studentMapper.toResponseList(
                studentRepo.findByPlacementLevel(placementLevel)
        );
    }

//    @Transactional
//    public StudentRes verifyEmail(String studentId) {
//        Student student = studentRepo.findById(studentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
//
//        student.setEmailVerified(true);
//
//        if (student.getStatus() == UserStatus.PENDING) {
//            student.setStatus(UserStatus.ACTIVE);
//        }
//
//        Student updated = studentRepo.save(student);
//        return studentMapper.toResponse(updated);
//    }

    @Transactional
    public StudentRes suspendStudent(String studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        student.setStatus(UserStatus.SUSPENDED);


        Student updated = studentRepo.save(student);
        return studentMapper.toResponse(updated);
    }

    @Transactional
    public StudentRes reactivateStudent(String studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        student.setStatus(UserStatus.ACTIVE);


        Student updated = studentRepo.save(student);
        return studentMapper.toResponse(updated);
    }

    @Transactional
    public StudentRes deactivateStudent(String studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        student.setStatus(UserStatus.INACTIVE);

        Student updated = studentRepo.save(student);
        return studentMapper.toResponse(updated);
    }

    @Transactional
    public void deleteStudent(String studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        // Initialize enrollments collection
        int enrollmentCount = student.getEnrollments().size();

        if (enrollmentCount > 0) {
            throw new IllegalStateException(
                    "Cannot delete student with enrollment history. " +
                            "Total enrollments: " + enrollmentCount + ". " +
                            "Please set to INACTIVE instead."
            );
        }

        if (student.getStatus() == UserStatus.ACTIVE ||
                student.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalStateException(
                    "Cannot delete student with status: " + student.getStatus() + ". " +
                            "Please set to INACTIVE first."
            );
        }

        if (student.getFirebaseUid() != null) {
            try {
                userMgmtService.deleteFirebaseUser(student.getFirebaseUid());
                log.info("Firebase user deleted:{}", student.getFirebaseUid());
            } catch (Exception e) {
                log.error("Failed to delete Firebase user: {}", e.getMessage());

            }
        }

        studentRepo.delete(student);

        log.info("Student deleted:{}", studentId);
    }
}