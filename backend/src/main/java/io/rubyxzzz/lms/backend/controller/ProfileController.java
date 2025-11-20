package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.request.UpdateProfileReq;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.model.UserRole;
import io.rubyxzzz.lms.backend.service.AdminService;
import io.rubyxzzz.lms.backend.service.InstructorService;
import io.rubyxzzz.lms.backend.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Profile Controller
 * Handles current user's profile operations for secured access
 */
@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final StudentService studentService;
    private final InstructorService instructorService;
    private final AdminService adminService;

    /**
     * Get current user's profile
     * GET /api/profile
     */
    @PreAuthorize("hasAuthority('PROFILE_VIEW_OWN')")
    @GetMapping
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal User currentUser
    ) {
        log.debug("Getting profile for user: {} ({})",
                currentUser.getEmail(), currentUser.getUserRole());

        switch (currentUser.getUserRole()) {
            case STUDENT:
                return ResponseEntity.ok(
                        studentService.getStudent(currentUser.getId())
                );
            case INSTRUCTOR:
                return ResponseEntity.ok(
                        instructorService.getInstructor(currentUser.getId())
                );
            case ADMIN:
                return ResponseEntity.ok(
                        adminService.getAdmin(currentUser.getId())
                );
            case APPLICANT:
                return ResponseEntity.ok(currentUser);
            default:
                return ResponseEntity.status(403).build();
        }
    }

    /**
     * Update current user's profile
     * PUT /api/profile
     */
    @PreAuthorize("hasAuthority('PROFILE_EDIT_OWN')")
    @PutMapping
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProfileReq request
    ) {
        log.debug("User updating own profile: {} ({})",
                currentUser.getEmail(), currentUser.getUserRole());

        switch (currentUser.getUserRole()) {
            case STUDENT:
                return ResponseEntity.ok(
                        studentService.updateStudentProfile(currentUser.getId(), request)
                );
            case INSTRUCTOR:
                return ResponseEntity.ok(
                        instructorService.updateInstructorProfile(currentUser.getId(), request)
                );
            case ADMIN:
                return ResponseEntity.ok(
                        adminService.updateAdminProfile(currentUser.getId(), request)
                );
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unavailable");
        }
    }

    @PreAuthorize("hasAuthority('PROFILE_VIEW_OWN')")
    @GetMapping("/with-enrollments")
    public ResponseEntity<?> getMyProfileWithEnrollments(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getUserRole() != UserRole.STUDENT) {
            return ResponseEntity.status(403)
                    .body("Only students can view enrollments");
        }

        return ResponseEntity.ok(
                studentService.getStudentWithEnrollments(currentUser.getId())
        );
    }

}
