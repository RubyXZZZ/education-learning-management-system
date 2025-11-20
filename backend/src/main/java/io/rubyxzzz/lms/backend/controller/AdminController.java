package io.rubyxzzz.lms.backend.controller;


import io.rubyxzzz.lms.backend.dto.listItem.AdminList;
import io.rubyxzzz.lms.backend.dto.request.CreateAdminReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateAdminReq;
import io.rubyxzzz.lms.backend.dto.response.AdminRes;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.model.UserStatus;
import io.rubyxzzz.lms.backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin REST Controller
 * Handles admin management endpoints
 */
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Create new admin
     * POST /api/admins
     */
    @PreAuthorize("hasAuthority('ADMINS_CREATE')")
    @PostMapping
    public ResponseEntity<AdminRes> createAdmin(
            @Valid @RequestBody CreateAdminReq request) {

        AdminRes admin = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }

    /**
     * Get admin by UUID
     * GET /api/admins/{id}
     */
    @PreAuthorize("hasAuthority('ADMINS_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<AdminRes> getAdmin(@PathVariable String id) {
        AdminRes admin = adminService.getAdmin(id);
        return ResponseEntity.ok(admin);
    }

    /**
     * Get all admins (detailed)
     * GET /api/admins/all
     */
    @PreAuthorize("hasAuthority('ADMINS_VIEW')")
    @GetMapping("/all")
    public ResponseEntity<List<AdminRes>> getAllAdmins() {
        List<AdminRes> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    /**
     * Get admins list (simplified)
     * GET /api/admins
     */
    @PreAuthorize("hasAuthority('ADMINS_VIEW')")
    @GetMapping
    public ResponseEntity<List<AdminList>> getAdminsList() {
        List<AdminList> admins = adminService.getAdminsList();
        return ResponseEntity.ok(admins);
    }

    /**
     * Get super admins
     * GET /api/admins/super-admins
     */
    @PreAuthorize("hasAuthority('ADMINS_VIEW')")
    @GetMapping("/super-admins")
    public ResponseEntity<List<AdminRes>> getSuperAdmins() {
        List<AdminRes> admins = adminService.getSuperAdmins();
        return ResponseEntity.ok(admins);
    }

    /**
     * Update admin information
     * PUT /api/admins/{id}
     */
    @PreAuthorize("hasAuthority('ADMINS_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<AdminRes> updateAdmin(
            @PathVariable String id,
            @Valid @RequestBody UpdateAdminReq request) {


        AdminRes admin = adminService.updateAdmin(id, request);
        return ResponseEntity.ok(admin);
    }


    /**
     * Promote admin to super admin
     * POST /api/admins/{id}/promote-super
     */
    @PreAuthorize("hasAuthority('ADMINS_PROMOTE_SUPER')")
    @PostMapping("/{id}/promote-super")
    public ResponseEntity<AdminRes> promoteToSuperAdmin(
            @PathVariable String id) {

        AdminRes admin = adminService.promoteToSuperAdmin(id);
        return ResponseEntity.ok(admin);
    }

    /**
     * Demote admin from super admin
     * POST /api/admins/{id}/demote-super
     */
    @PreAuthorize("hasAuthority('ADMINS_DEMOTE_SUPER')")
    @PostMapping("/{id}/demote-super")
    public ResponseEntity<AdminRes> demoteFromSuperAdmin(
            @PathVariable String id) {

        AdminRes admin = adminService.demoteFromSuperAdmin(id);
        return ResponseEntity.ok(admin);
    }


//    /**
//     * Verify admin email
//     * POST /api/admins/{id}/verify-email
//     */
//    @PreAuthorize("hasAuthority('ADMINS_VERIFY_EMAIL')")
//    @PostMapping("/{id}/verify-email")
//    public ResponseEntity<AdminRes> verifyEmail(
//            @PathVariable String id) {
//
//        AdminRes admin = adminService.verifyEmail(id);
//        return ResponseEntity.ok(admin);
//    }

    /**
     * Suspend admin
     * POST /api/admins/{id}/suspend
     */
    @PreAuthorize("hasAuthority('ADMINS_EDIT')")
    @PostMapping("/{id}/suspend")
    public ResponseEntity<AdminRes> suspendAdmin(
            @PathVariable String id) {

        AdminRes admin = adminService.suspendAdmin(id);
        return ResponseEntity.ok(admin);
    }

    /**
     * Reactivate admin
     * POST /api/admins/{id}/reactivate
     */
    @PreAuthorize("hasAuthority('ADMINS_EDIT')")
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<AdminRes> reactivateAdmin(
            @PathVariable String id) {

        AdminRes admin = adminService.reactivateAdmin(id);
        return ResponseEntity.ok(admin);
    }

    /**
     * Deactivate admin
     * POST /api/admins/{id}/deactivate
     */
    @PreAuthorize("hasAuthority('ADMINS_EDIT')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<AdminRes> deactivateAdmin(
            @PathVariable String id) {

        AdminRes admin = adminService.deactivateAdmin(id);
        return ResponseEntity.ok(admin);
    }

    /**
     * Delete admin (hard delete)
     * DELETE /api/admins/{id}
     */
    @PreAuthorize("hasAuthority('ADMINS_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get admin by employee number
     * GET /api/admins/by-employee/{employeeNumber}
     */
    @PreAuthorize("hasAuthority('ADMINS_VIEW')")
    @GetMapping("/by-employee/{employeeNumber}")
    public ResponseEntity<AdminRes> getAdminByEmployeeNumber(
            @PathVariable String employeeNumber) {
        AdminRes admin = adminService.getAdminByEmployeeNumber(employeeNumber);
        return ResponseEntity.ok(admin);
    }

    /**
     * Get admins by department
     * GET /api/admins/department/{department}
     */
    @PreAuthorize("hasAuthority('ADMINS_VIEW')")
    @GetMapping("/department/{department}")
    public ResponseEntity<List<AdminRes>> getAdminsByDepartment(
            @PathVariable String department) {
        List<AdminRes> admins = adminService.getAdminsByDepartment(department);
        return ResponseEntity.ok(admins);
    }

    /**
     * Get admins by status
     * GET /api/admins/status/{status}
     */
    @PreAuthorize("hasAuthority('ADMINS_VIEW')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AdminRes>> getAdminsByStatus(
            @PathVariable UserStatus status) {
        List<AdminRes> admins = adminService.getAdminsByStatus(status);
        return ResponseEntity.ok(admins);
    }

}

