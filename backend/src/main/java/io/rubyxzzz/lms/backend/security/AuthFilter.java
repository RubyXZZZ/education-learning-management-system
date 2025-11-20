package io.rubyxzzz.lms.backend.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.rubyxzzz.lms.backend.model.*;
import io.rubyxzzz.lms.backend.repository.AdminRepo;
import io.rubyxzzz.lms.backend.repository.InstructorRepo;
import io.rubyxzzz.lms.backend.repository.StudentRepo;
import io.rubyxzzz.lms.backend.service.FirebaseAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.Set;

import java.io.IOException;
import java.util.List;

/**
 * Firebase Authentication Filter
 * Intercepts requests and validates Firebase tokens
 *
 * Flow:
 * 1. Extract Firebase token from Authorization header
 * 2. Verify token with Firebase
 * 3. Find user in local database
 * 4. Set authentication in SecurityContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseAuthService firebaseAuthService;
    private final StudentRepo studentRepo;
    private final InstructorRepo instructorRepo;
    private final AdminRepo adminRepo;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Extract token from Authorization header
            String firebaseToken = extractToken(request);

            if (firebaseToken != null) {
                // Verify Firebase token
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseToken);
                String firebaseUid = decodedToken.getUid();

                if (firebaseUid != null) {
                    // Find user in local database
                    User user = firebaseAuthService.findUserByFirebaseUid(firebaseUid);

                    // check user status
                    if (user != null) {
                        if (decodedToken.isEmailVerified() && !user.getEmailVerified()) {
                            user.setEmailVerified(true);
                            // uto-activate PENDING users
                            if (user.getStatus() == UserStatus.PENDING) {
                                user.setStatus(UserStatus.ACTIVE);
                            }
                            // save changes
                            if (user instanceof Student) {
                                studentRepo.save((Student) user);
                            } else if (user instanceof Instructor) {
                                instructorRepo.save((Instructor) user);
                            } else if (user instanceof Admin) {
                                adminRepo.save((Admin) user);
                            }

                            log.info("User auto-activated: {}", user.getEmail());
                        }

                        // only allow active users to login
                        if (user.getStatus() != UserStatus.ACTIVE){
                            log.warn("User login blocked - Status: {}, Email: {}",
                                    user.getStatus(), user.getEmail());
                        } else {
                            // create authorities list: role + permissions
                            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                            // add role (for role-based checks)
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));

                            // add all permissions for this user ( including superadmin check)
                            Set<Permission> permissions = RolePermissionMapping.getPermissions(user);
                            for (Permission permission : permissions) {
                                authorities.add(new SimpleGrantedAuthority(permission.name()));
                            }

                            // debug
                            //log.info(" == Authorities loaded: {}", authorities);

                            // Create authentication object
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            user,
                                            null,
                                            authorities
                                    );

                            authentication.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );

                            // Set authentication in security context
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            log.debug("User authenticated: {} ({}) with {} permissions",
                                    user.getEmail(),
                                    user.getUserRole(),
                                    permissions.size());
                        }

                    } else {
                        log.warn("User not found or email not verified for Firebase UID: {}", firebaseUid);
                    }
                }
            }

        } catch (FirebaseAuthException e) {
            log.error("Firebase token verification failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error in Firebase auth filter: {}", e.getMessage());
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract Firebase token from Authorization header
     * Format: "Bearer <token>"
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}