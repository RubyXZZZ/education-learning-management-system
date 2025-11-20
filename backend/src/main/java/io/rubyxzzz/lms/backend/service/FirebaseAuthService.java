package io.rubyxzzz.lms.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.rubyxzzz.lms.backend.exception.AuthenticationException;
import io.rubyxzzz.lms.backend.model.*;
import io.rubyxzzz.lms.backend.repository.AdminRepo;
import io.rubyxzzz.lms.backend.repository.InstructorRepo;
import io.rubyxzzz.lms.backend.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Firebase Authentication Service
 * Handles Firebase token verification and user management
 *
 * Responsibilities:
 * - Verify Firebase ID tokens
 * - Find or create users based on Firebase authentication
 * - Map Firebase users to local User entities
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;
    private final StudentRepo studentRepo;
    private final InstructorRepo instructorRepo;
    private final AdminRepo adminRepo;

    /**
     * Verify Firebase ID token and return user info
     */
    public FirebaseToken verifyToken(String firebaseToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseToken);
            log.info("Firebase token verified for user: {}", decodedToken.getEmail());
            return decodedToken;

        } catch (FirebaseAuthException e) {
            log.error("Firebase token verification failed: {}", e.getMessage());
            throw new AuthenticationException("Invalid Firebase token: " + e.getMessage());
        }
    }

    /**
     * Find user by firbaseUid
     * Returns the User entity (Student, Instructor, or Admin)
     */
    @Transactional(readOnly = true)
    public User findUserByFirebaseUid(String firebaseUid) {
        User user = studentRepo.findByFirebaseUid(firebaseUid).orElse(null);
        if (user != null) return user;

        user = instructorRepo.findByFirebaseUid(firebaseUid).orElse(null);
        if (user != null) return user;

        return adminRepo.findByFirebaseUid(firebaseUid).orElse(null);
    }


    /**
     * Authenticate user with Firebase token
     * Verifies token and finds corresponding user
     */
    @Transactional(readOnly = true)
    public User authenticateWithFirebase(String firebaseToken) {
        // Verify Firebase token
        FirebaseToken decodedToken = verifyToken(firebaseToken);
        String firebaseUid = decodedToken.getUid();

        // Find user in local database
        User user = findUserByFirebaseUid(firebaseUid);

        if (user == null) {
            throw new AuthenticationException("No user found with Firebase UID: " + firebaseUid);
        }

        // Check if user is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthenticationException(
                    "User account is not active. Status: " + user.getStatus()
            );
        }

        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new AuthenticationException(
                    "Email not verified. Please verify your email first."
            );
        }

        log.info("User authenticated successfully: {} ({})",
                user.getEmail(), user.getUserRole());

        return user;
    }
}