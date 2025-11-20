package io.rubyxzzz.lms.backend.service;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * User Management Service
 * Handles Firebase user creation and management
 *
 * Used by Admin when creating Student/Instructor/Admin accounts
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMgmtService {

    private final FirebaseAuth firebaseAuth;

    /**
     * Create Firebase user and send verification email
     *
     * Flow:
     * 1. Admin creates user in local database
     * 2. Call this method to create Firebase user
     * 3. Firebase sends password setup email to user
     * 4. User clicks link and sets password
     * 5. Email automatically verified
     */
    public String createFirebaseUser(String email, String displayName) {
        try {
            // Create user in Firebase
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setDisplayName(displayName)
                    .setEmailVerified(false);  // User needs to verify via email

            ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                    .setUrl("http://localhost:3000/login")
                    .setHandleCodeInApp(false)
                    .build();

            UserRecord userRecord = firebaseAuth.createUser(request);

            String firebaseUid = userRecord.getUid();

            log.info("✅ Firebase user created: {} (UID: {})", email, userRecord.getUid());
            log.info("   - DisplayName: {}", userRecord.getDisplayName());

            // Generate password reset link (acts as "set password" link)
            String resetLink = firebaseAuth.generatePasswordResetLink(
                    email,
                    actionCodeSettings
            );

            System.out.println("=".repeat(10));
            System.out.println("📧 ACTIVATION EMAIL");
            System.out.println("To: " + email);
            System.out.println("Name: " + displayName);
            System.out.println("🔗 Password Setup Link:");
            System.out.println(resetLink);
            System.out.println("=".repeat(10));

            log.info("📧 Password setup link generated for: {}", email);

            // TODO: Send email with resetLink to user by email service
            // emailService.sendWelcomeEmail(email, displayName, resetLink);



            return firebaseUid;

        } catch (FirebaseAuthException e) {
            log.error("❌ Failed to create Firebase user: {}", e.getMessage());
            throw new RuntimeException("Failed to create Firebase user: " + e.getMessage());
        }
    }


    public void deleteFirebaseUser(String firebaseUid) {
        try {
            firebaseAuth.deleteUser(firebaseUid);
            log.info("✅ Firebase user deleted: {}", firebaseUid);

        } catch (FirebaseAuthException e) {
            log.warn("❌ Failed to delete Firebase user (may not exist): {}", firebaseUid);
        }
    }


    public void updateFirebaseUserEmail(String firebaseUid, String newEmail) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(firebaseUid)
                    .setEmail(newEmail)
                    .setEmailVerified(false);

            firebaseAuth.updateUser(request);

            log.info("✅ Firebase user email updated to: {}", newEmail);

        } catch (FirebaseAuthException e) {
            log.error("❌ Failed to update Firebase user email: {}", e.getMessage());
            throw new RuntimeException("Failed to update Firebase user email: " + e.getMessage());
        }
    }


    public void sendPasswordResetEmail(String email) {
        try {
            String resetLink = firebaseAuth.generatePasswordResetLink(email);

            log.info("Password reset link generated for: {}", email);

        } catch (FirebaseAuthException e) {
            log.error("❌ Failed to generate password reset link: {}", e.getMessage());
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }
}