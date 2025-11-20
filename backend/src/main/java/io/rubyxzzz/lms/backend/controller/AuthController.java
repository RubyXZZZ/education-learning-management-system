package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.request.LoginReq;
import io.rubyxzzz.lms.backend.dto.response.LoginRes;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles login, logout, and user info endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Login with Firebase token
     * POST /api/auth/login
     *
     * Frontend flow:
     * 1. User logs in with Firebase (email + password)
     * 2. Frontend gets Firebase ID token
     * 3. Frontend sends token to this endpoint
     * 4. Backend verifies token and returns user info
     */
    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@Valid @RequestBody LoginReq request) {
        LoginRes response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user
     * GET /api/auth/me
     *
     * Returns current user information from Firebase token
     */
    @GetMapping("/me")
    public ResponseEntity<LoginRes> getCurrentUser(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        LoginRes response = authService.getCurrentUser(currentUser.getFirebaseUid());
        return ResponseEntity.ok(response);
    }

    /**
     * Logout
     * POST /api/auth/logout
     *
     * Firebase tokens are stateless, so logout is handled on frontend
     * This endpoint is optional and just for logging purposes
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser != null) {
            authService.logout(currentUser.getId());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Health check endpoint
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }
}