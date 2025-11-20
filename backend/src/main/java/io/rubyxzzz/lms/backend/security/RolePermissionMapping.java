package io.rubyxzzz.lms.backend.security;

import io.rubyxzzz.lms.backend.model.Admin;
import io.rubyxzzz.lms.backend.model.Permission;
import io.rubyxzzz.lms.backend.model.User;
import io.rubyxzzz.lms.backend.model.UserRole;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Role-Permission Mapping
 * Defines which permissions each role has in the system
 *
 * Permission Design:
 * - VIEW permissions include both list and detail views (frontend controls specific display)
 * - EDIT permissions include all status changes and related operations
 * - Super Admin exclusive permissions handle critical system management
 */
public class RolePermissionMapping {

    /**
     * Base permissions for each role
     * These are the permissions available to regular users of each role
     */
    private static final Map<UserRole, Set<Permission>> ROLE_PERMISSIONS = Map.of(

            // ===== ADMIN - System Management (Regular Admin) =====
            UserRole.ADMIN, Set.of(
                    // Students Management
                    Permission.STUDENTS_VIEW,
                    Permission.STUDENTS_CREATE,
                    Permission.STUDENTS_EDIT,
                    Permission.STUDENTS_DELETE,

                    // Instructors Management
                    Permission.INSTRUCTORS_VIEW,
                    Permission.INSTRUCTORS_CREATE,
                    Permission.INSTRUCTORS_EDIT,
                    Permission.INSTRUCTORS_DELETE,

                    // Courses Management
                    Permission.COURSES_VIEW,
                    Permission.COURSES_CREATE,
                    Permission.COURSES_EDIT,
                    Permission.COURSES_DELETE,
                    Permission.COURSES_MANAGE_SECTIONS,

                    // Sections Management
                    Permission.SECTIONS_VIEW,
                    Permission.SECTIONS_CREATE,
                    Permission.SECTIONS_EDIT,
                    Permission.SECTIONS_DELETE,

                    // Sessions (View only - for course creation)
                    Permission.SESSIONS_VIEW,

                    // Enrollments Management
                    Permission.ENROLLMENTS_VIEW_ALL,
                    Permission.ENROLLMENTS_CREATE,
                    Permission.ENROLLMENTS_DROP,
                    Permission.ENROLLMENTS_GRADE,

                    // Course Content Management
                    Permission.MODULES_VIEW_ALL,
                    Permission.MODULES_CREATE,
                    Permission.MODULES_EDIT,
                    Permission.MODULES_DELETE,

                    Permission.PAGES_VIEW_ALL,
                    Permission.PAGES_CREATE,
                    Permission.PAGES_EDIT,
                    Permission.PAGES_DELETE,

                    Permission.ASSIGNMENTS_VIEW_ALL,
                    Permission.ASSIGNMENTS_CREATE,
                    Permission.ASSIGNMENTS_EDIT,
                    Permission.ASSIGNMENTS_DELETE,

                    // Submissions Management
                    Permission.SUBMISSIONS_VIEW_ALL,
                    Permission.SUBMISSIONS_GRADE,

                    // Profile
                    Permission.PROFILE_VIEW_OWN,
                    Permission.PROFILE_EDIT_OWN
            ),

            // ===== INSTRUCTOR - Teaching Access =====
            UserRole.INSTRUCTOR, Set.of(
                    // View students in their sections
                    Permission.STUDENTS_VIEW,

                    // View instructors (colleagues)
                    Permission.INSTRUCTORS_VIEW,

                    // View courses/sections/sessions
                    Permission.COURSES_VIEW,
                    Permission.SECTIONS_VIEW,
                    Permission.SESSIONS_VIEW,

                    // Enrollment Management (for their sections)
                    Permission.ENROLLMENTS_VIEW_SECTION,
                    Permission.ENROLLMENTS_GRADE,

                    // Course Content Management (for their sections)
                    Permission.MODULES_VIEW_ALL,
                    Permission.MODULES_CREATE,
                    Permission.MODULES_EDIT,
                    Permission.MODULES_DELETE,

                    Permission.PAGES_VIEW_ALL,
                    Permission.PAGES_CREATE,
                    Permission.PAGES_EDIT,
                    Permission.PAGES_DELETE,

                    Permission.ASSIGNMENTS_VIEW_ALL,
                    Permission.ASSIGNMENTS_CREATE,
                    Permission.ASSIGNMENTS_EDIT,
                    Permission.ASSIGNMENTS_DELETE,

                    // Submission Management (for their sections)
                    Permission.SUBMISSIONS_VIEW_ALL,
                    Permission.SUBMISSIONS_GRADE,

                    // Profile
                    Permission.PROFILE_VIEW_OWN,
                    Permission.PROFILE_EDIT_OWN
            ),

            // ===== STUDENT - Learning Access =====
            UserRole.STUDENT, Set.of(
                    // View available courses/sections/sessions
                    Permission.COURSES_VIEW,
                    Permission.SECTIONS_VIEW,
                    Permission.SESSIONS_VIEW,

                    // View own enrollments
                    Permission.ENROLLMENTS_CREATE,
                    Permission.ENROLLMENTS_DROP,
                    Permission.ENROLLMENTS_VIEW_OWN,
                    Permission.ENROLLMENTS_VIEW_SECTION,

                    // View published course content only
                    Permission.MODULES_VIEW_PUBLISHED,
                    Permission.PAGES_VIEW_PUBLISHED,
                    Permission.ASSIGNMENTS_VIEW_PUBLISHED,

                    // Submission Management (own)
                    Permission.SUBMISSIONS_VIEW_OWN,
                    Permission.SUBMISSIONS_CREATE,

                    // Profile
                    Permission.PROFILE_VIEW_OWN,
                    Permission.PROFILE_EDIT_OWN
            )
    );

    //Super Admin Only Permissions
    private static final Set<Permission> SUPER_ADMIN_ONLY_PERMISSIONS = Set.of(
            // Admin Management (full control)
            Permission.ADMINS_VIEW,             // View all admins
            Permission.ADMINS_CREATE,           // Create new admin accounts
            Permission.ADMINS_EDIT,             // Edit admin information
            Permission.ADMINS_DELETE,           // Delete admin accounts
            Permission.ADMINS_PROMOTE_SUPER,    // Promote regular admin to super admin
            Permission.ADMINS_DEMOTE_SUPER,     // Demote super admin to regular admin

            // Session Management (academic calendar control)
            Permission.SESSIONS_CREATE,         // Create new academic sessions
            Permission.SESSIONS_EDIT            // Edit session dates/status
    );

    //Get all permissions for a user (including super admin permissions if applicable)
    public static Set<Permission> getPermissions(User user) {
        Set<Permission> permissions = new HashSet<>(getBasePermissions(user.getUserRole()));

        // Add super admin permissions if applicable
        if (isSuperAdmin(user)) {
            permissions.addAll(SUPER_ADMIN_ONLY_PERMISSIONS);
        }

        return permissions;
    }

    //Check if a user has a specific permission
    public static boolean hasPermission(User user, Permission permission) {
        return getPermissions(user).contains(permission);
    }

    // Check if a user is super admin
    public static boolean isSuperAdmin(User user) {
        if (user.getUserRole() != UserRole.ADMIN || !(user instanceof Admin)) {
            return false;
        }
        Admin admin = (Admin) user;
        return admin.getIsSuperAdmin() != null && admin.getIsSuperAdmin();
    }

    //Get base permissions for a role (internal use only) , not including super admin
    private static Set<Permission> getBasePermissions(UserRole role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }
}