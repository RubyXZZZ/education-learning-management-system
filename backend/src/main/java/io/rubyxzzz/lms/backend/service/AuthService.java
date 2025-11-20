package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.request.LoginReq;
import io.rubyxzzz.lms.backend.dto.response.LoginRes;
import io.rubyxzzz.lms.backend.model.Admin;
import io.rubyxzzz.lms.backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 * Handles login and user authentication
 *
 * Flow:
 * 1. Receive Firebase token from frontend
 * 2. Verify token and get user
 * 3. Return user information
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final FirebaseAuthService firebaseAuthService;

    /**
     * Login with Firebase token
     *
     * Frontend sends Firebase ID token after user logs in with Firebase
     * Backend verifies token and returns user info
     */
    public LoginRes login(LoginReq request) {
        log.info("Processing login request");

        // Authenticate with Firebase and get user
        User user = firebaseAuthService.authenticateWithFirebase(
                request.getLoginToken()
        );

        log.info("Login successful for user: {} ({})",
                user.getEmail(), user.getUserRole());

        // Build response using BeanUtils
        LoginRes response = new LoginRes();
        BeanUtils.copyProperties(user, response);

        // Set fields that BeanUtils cannot auto-copy
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setUserNumber(user.getUserNumber());
        response.setRole(user.getUserRole());

        return response;
    }

    /**
     * Get current user info
     */
    public LoginRes getCurrentUser(String firebaseUid) {
        User user = firebaseAuthService.findUserByFirebaseUid(firebaseUid);

        if (user == null) {
            return null;
        }

        LoginRes response = new LoginRes();
        BeanUtils.copyProperties(user, response);

        // Set fields that BeanUtils cannot auto-copy
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setUserNumber(user.getUserNumber());
        response.setRole(user.getUserRole());

        if (user instanceof Admin) {
            response.setIsSuperAdmin(((Admin) user).getIsSuperAdmin());
        } else {
            response.setIsSuperAdmin(false);
        }

        return response;
    }

    /**
     * Logout
     * Firebase tokens are stateless, so logout is handled on frontend
     */
    public void logout(String userId) {
        log.info("User logged out: {}", userId);
    }
}