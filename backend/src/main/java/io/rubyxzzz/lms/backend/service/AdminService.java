package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.listItem.AdminList;
import io.rubyxzzz.lms.backend.dto.request.CreateAdminReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateAdminReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateProfileReq;
import io.rubyxzzz.lms.backend.dto.response.AdminRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.AdminMapper;
import io.rubyxzzz.lms.backend.model.Admin;
import io.rubyxzzz.lms.backend.model.UserStatus;
import io.rubyxzzz.lms.backend.repository.AdminRepo;
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
public class AdminService {
    private final AdminRepo adminRepo;
    private final AdminMapper adminMapper;
    private final UserMgmtService userMgmtService;

    @Transactional
    public AdminRes createAdmin(CreateAdminReq request) {
        // Validate email uniqueness
        if (adminRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists: " + request.getEmail()
            );
        }

        // Validate employee number uniqueness
        if (adminRepo.existsByEmployeeNumber(request.getEmployeeNumber())) {
            throw new IllegalArgumentException(
                    "Employee number already exists: " + request.getEmployeeNumber()
            );
        }

        String firebaseUid = null;

        try {
            // 1. Create Firebase user first to get firebaseUid
            firebaseUid = userMgmtService.createFirebaseUser(
                    request.getEmail(),
                    request.getFirstName() + " " + request.getLastName()
            );

            // 2. Create Admin entity
            Admin admin = new Admin();
            admin.setFirebaseUid(firebaseUid);

            // Copy common fields from request
            BeanUtils.copyProperties(request, admin);

            // Handle isSuperAdmin (with default false)
            if (request.getIsSuperAdmin() != null) {
                admin.setIsSuperAdmin(request.getIsSuperAdmin());
            } else {
                admin.setIsSuperAdmin(false);
            }

            // 3. Save admin
            Admin savedAdmin = adminRepo.save(admin);
            return adminMapper.toResponse(savedAdmin);

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
            throw new RuntimeException("Failed to create admin: " + e.getMessage(), e);
        }
    }
    @Transactional
    public AdminRes updateAdmin(String adminId, UpdateAdminReq request) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        UpdateUtil.copyNonNullProperties(request, admin);


        Admin updatedAdmin = adminRepo.save(admin);
        return adminMapper.toResponse(updatedAdmin);
    }

    // use for updating own profile
    @Transactional
    public AdminRes updateAdminProfile(String adminId, UpdateProfileReq request) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        // Only update fields that exist in Admin
        if (request.getPhone() != null) {
            admin.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            admin.setAddress(request.getAddress());
        }

        Admin updated = adminRepo.save(admin);
        return adminMapper.toResponse(updated);
    }

    public AdminRes getAdmin(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));
        return adminMapper.toResponse(admin);
    }

    public List<AdminRes> getAllAdmins() {
        return adminMapper.toResponseList(adminRepo.findAll());
    }

    public List<AdminRes> getSuperAdmins() {
        return adminMapper.toResponseList(adminRepo.findSuperAdmins());
    }

    public List<AdminList> getAdminsList() {
        return adminMapper.toListItems(adminRepo.findAll());
    }

    /**
     * Get admin by employee number
     */
    public AdminRes getAdminByEmployeeNumber(String employeeNumber) {
        Admin admin = adminRepo.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin", "employeeNumber", employeeNumber
                ));
        return adminMapper.toResponse(admin);
    }

    /**
     * Get admins by department
     */
    public List<AdminRes> getAdminsByDepartment(String department) {
        return adminMapper.toResponseList(
                adminRepo.findByDepartment(department)
        );
    }

    /**
     * Get admins by status
     */
    public List<AdminRes> getAdminsByStatus(UserStatus status) {
        return adminMapper.toResponseList(
                adminRepo.findByStatus(status)
        );
    }


    /**
     * Promote admin to super admin
     */
    @Transactional
    public AdminRes promoteToSuperAdmin(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        admin.promoteToSuperAdmin();

        Admin updated = adminRepo.save(admin);
        return adminMapper.toResponse(updated);
    }

    /**
     * Demote admin from super admin
     */
    @Transactional
    public AdminRes demoteFromSuperAdmin(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        admin.demoteFromSuperAdmin();

        Admin updated = adminRepo.save(admin);
        return adminMapper.toResponse(updated);
    }


    /**
     * Verify admin email
     */
//    @Transactional
//    public AdminRes verifyEmail(String adminId) {
//        Admin admin = adminRepo.findById(adminId)
//                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));
//
//        admin.setEmailVerified(true);
//
//        if (admin.getStatus() == UserStatus.PENDING) {
//            admin.setStatus(UserStatus.ACTIVE);
//        }
//
//        Admin updated = adminRepo.save(admin);
//        return adminMapper.toResponse(updated);
//    }

    /**
     * Suspend admin
     */
    @Transactional
    public AdminRes suspendAdmin(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        // Cannot suspend super admin
        if (admin.isSuperAdmin()) {
            throw new IllegalStateException("Cannot suspend super admin");
        }

        admin.setStatus(UserStatus.SUSPENDED);

        Admin updated = adminRepo.save(admin);
        return adminMapper.toResponse(updated);
    }

    /**
     * Reactivate admin
     */
    @Transactional
    public AdminRes reactivateAdmin(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        admin.setStatus(UserStatus.ACTIVE);

        Admin updated = adminRepo.save(admin);
        return adminMapper.toResponse(updated);
    }

    /**
     * Set admin to inactive
     */
    @Transactional
    public AdminRes deactivateAdmin(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        // Cannot inactivate super admin
        if (admin.isSuperAdmin()) {
            throw new IllegalStateException("Cannot set super admin to inactive");
        }

        admin.setStatus(UserStatus.INACTIVE);

        Admin updated = adminRepo.save(admin);
        return adminMapper.toResponse(updated);
    }

    /**
     * Delete admin (hard delete with validation)
     */
    @Transactional
    public void deleteAdmin(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        // Cannot delete super admin
        if (admin.isSuperAdmin()) {
            throw new IllegalStateException("Cannot delete super admin");
        }

        // Validate: can only delete PENDING or INACTIVE status
        if (admin.getStatus() == UserStatus.ACTIVE ||
                admin.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalStateException(
                    "Cannot delete admin with status: " + admin.getStatus() + ". " +
                            "Please set to INACTIVE first."
            );
        }

        if (admin.getFirebaseUid()!=null) {
            // Delete Firebase user
            try {
                userMgmtService.deleteFirebaseUser(admin.getFirebaseUid());
                log.info("Firebase user deleted:{}", admin.getFirebaseUid());
            } catch (Exception e) {
                log.error("Failed to delete Firebase user during admin deletion: {}", e.getMessage());
            }
        }

        // Hard delete
        adminRepo.delete(admin);

        log.info("Admin deleted: {}", adminId);
    }
}