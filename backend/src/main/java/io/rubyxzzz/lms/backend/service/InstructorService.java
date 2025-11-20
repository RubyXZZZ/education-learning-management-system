package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.listItem.InstructorList;
import io.rubyxzzz.lms.backend.dto.request.CreateInstructorReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateInstructorReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateProfileReq;
import io.rubyxzzz.lms.backend.dto.response.InstructorRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.InstructorMapper;
import io.rubyxzzz.lms.backend.model.Instructor;
import io.rubyxzzz.lms.backend.model.UserStatus;
import io.rubyxzzz.lms.backend.repository.InstructorRepo;
import io.rubyxzzz.lms.backend.util.IdGenerator;

import io.rubyxzzz.lms.backend.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepo instructorRepo;
    private final InstructorMapper instructorMapper;
    private final IdGenerator idGenerator;
//    private final PasswordEncoder passwordEncoder;
    private final UserMgmtService userMgmtService;

    @Transactional
    public InstructorRes createInstructor(CreateInstructorReq request) {
        if (instructorRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists: " + request.getEmail()
            );
        }

        if (instructorRepo.existsByEmployeeNumber(request.getEmployeeNumber())) {
            throw new IllegalArgumentException(
                    "Employee number already exists: " + request.getEmployeeNumber()
            );
        }

        String firebaseUid = null;

        try {
            // 1. Create Firebase user first to get firebaseUid
            firebaseUid = userMgmtService.createFirebaseUser(
                    request.getEmail(),
                    request.getFirstName() + " " + request.getLastName());

            // 2. Create Instructor entity
            Instructor instructor = new Instructor();

            instructor.setFirebaseUid(firebaseUid);

            BeanUtils.copyProperties(request, instructor);

            // 3. Save instructor
            Instructor savedInstructor = instructorRepo.save(instructor);
            return instructorMapper.toResponse(savedInstructor);

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
            throw new RuntimeException("Failed to create instructor: " + e.getMessage(), e);
        }
    }

    @Transactional
    public InstructorRes updateInstructor(String instructorId, UpdateInstructorReq request) {
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        UpdateUtil.copyNonNullProperties(request, instructor);



        Instructor updatedInstructor = instructorRepo.save(instructor);
        return instructorMapper.toResponse(updatedInstructor);
    }

    // use for updating own profile
    @Transactional
    public InstructorRes updateInstructorProfile(String instructorId, UpdateProfileReq request) {
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));

        // Only update fields that exist in Instructor
        if (request.getPhone() != null) {
            instructor.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            instructor.setAddress(request.getAddress());
        }

        Instructor updated = instructorRepo.save(instructor);
        return instructorMapper.toResponse(updated);
    }


    /**
     * Get instructor basic info (no sections)
     */
    public InstructorRes getInstructor(String instructorId) {
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));
        return instructorMapper.toResponse(instructor);
    }

    /**
     * Get instructor with all teaching sections
     * Returns all historical sections
     */
    @Transactional(readOnly = true)
    public InstructorRes getInstructorWithSections(String instructorId) {
        Instructor instructor = instructorRepo.findByIdWithRelations(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));

        // sections already loaded by JOIN FETCH
        return instructorMapper.toResponseWithSections(instructor);
    }



    public InstructorRes getInstructorByEmployeeNumber(String employeeNumber) {
        Instructor instructor = instructorRepo.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor", "employeeNumber", employeeNumber
                ));
        return instructorMapper.toResponse(instructor);
    }

    public List<InstructorRes> getAllInstructors() {
        return instructorMapper.toResponseList(instructorRepo.findAll());
    }

    public List<InstructorRes> getInstructorsByDepartment(String department) {
        return instructorMapper.toResponseList(instructorRepo.findByDepartment(department));
    }

    public List<InstructorList> getInstructorsList() {
        return instructorMapper.toListItems(instructorRepo.findAll());
    }

    public List<InstructorRes> getInstructorsByStatus(UserStatus status) {
        return instructorMapper.toResponseList(
                instructorRepo.findByStatus(status)
        );
    }

//    @Transactional
//    public InstructorRes verifyEmail(String instructorId) {
//        Instructor instructor = instructorRepo.findById(instructorId)
//                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));
//
//        instructor.setEmailVerified(true);
//
//        if (instructor.getStatus() == UserStatus.PENDING) {
//            instructor.setStatus(UserStatus.ACTIVE);
//        }
//
//        Instructor updated = instructorRepo.save(instructor);
//        return instructorMapper.toResponse(updated);
//    }

    @Transactional
    public InstructorRes suspendInstructor(String instructorId) {
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));

        instructor.setStatus(UserStatus.SUSPENDED);

        Instructor updated = instructorRepo.save(instructor);
        return instructorMapper.toResponse(updated);
    }

    @Transactional
    public InstructorRes reactivateInstructor(String instructorId) {
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));

        instructor.setStatus(UserStatus.ACTIVE);

        Instructor updated = instructorRepo.save(instructor);
        return instructorMapper.toResponse(updated);
    }

    @Transactional
    public InstructorRes deactivateInstructor(String instructorId) {
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));

        instructor.setStatus(UserStatus.INACTIVE);

        Instructor updated = instructorRepo.save(instructor);
        return instructorMapper.toResponse(updated);
    }

    @Transactional
    public void deleteInstructor(String instructorId) {
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", instructorId));

        if (instructor.getTeachingCounts() != null && instructor.getTeachingCounts() > 0) {
            throw new IllegalStateException(
                    "Cannot delete instructor with teaching history. " +
                            "Current teaching load: " + instructor.getTeachingCounts() + ". " +
                            "Please set to INACTIVE instead."
            );
        }

        if (instructor.getStatus() == UserStatus.ACTIVE ||
                instructor.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalStateException(
                    "Cannot delete instructor with status: " + instructor.getStatus() + ". " +
                            "Please set to INACTIVE first."
            );
        }

        if (instructor.getFirebaseUid() != null) {
            try {
                userMgmtService.deleteFirebaseUser(instructor.getFirebaseUid());
            } catch (Exception e) {
                log.error("Failed to delete Firebase user: {}", e.getMessage());
            }
        }

        instructorRepo.delete(instructor);

        log.info("Instructor deleted:{}", instructorId);
    }
}