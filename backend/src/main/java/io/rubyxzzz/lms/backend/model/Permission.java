package io.rubyxzzz.lms.backend.model;

/**
 * System Permission Enumeration
 * Defines all available permissions in the LMS
 * Naming Convention: MODULE_ACTION
 */
public enum Permission {

    // ===== Students Management =====
    STUDENTS_VIEW,              // View student list/details
    STUDENTS_CREATE,            // Create new student
    STUDENTS_EDIT,              // Edit student (includes status changes, email verification)
    STUDENTS_DELETE,            // Delete student

    // ===== Instructors Management =====
    INSTRUCTORS_VIEW,
    INSTRUCTORS_CREATE,
    INSTRUCTORS_EDIT,           // Edit instructor (includes status changes)
    INSTRUCTORS_DELETE,

    // ===== Admins Management（super admin can mng =====
    ADMINS_VIEW,
    ADMINS_CREATE,
    ADMINS_EDIT,                 //Edit admin (includes status changes)
    ADMINS_DELETE,
    ADMINS_PROMOTE_SUPER,       //  Promote to super admin
    ADMINS_DEMOTE_SUPER,        // Demote from super admin

    // ===== Courses (Templates) =====
    COURSES_VIEW,
    COURSES_CREATE,
    COURSES_EDIT,               // Edit course (includes activate/deactivate)
    COURSES_DELETE,
    COURSES_MANAGE_SECTIONS,    // Add/remove sections to/from course

    // ===== Sections (Course Instances) =====
    SECTIONS_VIEW,
    SECTIONS_CREATE,
    SECTIONS_EDIT,              // Edit section (includes publish/cancel)
    SECTIONS_DELETE,

    // ===== Sessions (Terms) =====
    SESSIONS_VIEW,
    SESSIONS_CREATE,            // ⭐ Super Admin only
    SESSIONS_EDIT,              // ⭐ Super Admin only

    // ===== Enrollments =====
    ENROLLMENTS_VIEW_ALL,       // Admin: view all enrollments
    ENROLLMENTS_VIEW_SECTION,   // Instructor: view enrollments in their sections
    ENROLLMENTS_VIEW_OWN,       // Student: view own enrollments
    ENROLLMENTS_CREATE,         // Enroll student in section
    ENROLLMENTS_DROP,           // Drop enrollment
    ENROLLMENTS_GRADE,          // Complete enrollment with final grade

    // ===== Modules =====
    MODULES_VIEW_ALL,           // Instructor: view all modules (including unpublished)
    MODULES_VIEW_PUBLISHED,     // Student: view published modules only
    MODULES_CREATE,
    MODULES_EDIT,               // Edit module (includes publish/unpublish, reorder)
    MODULES_DELETE,

    // ===== Course Pages =====
    PAGES_VIEW_ALL,             // Instructor: view all pages
    PAGES_VIEW_PUBLISHED,       // Student: view published pages only
    PAGES_CREATE,
    PAGES_EDIT,                 // Edit page (includes publish/unpublish)
    PAGES_DELETE,

    // ===== Assignments =====
    ASSIGNMENTS_VIEW_ALL,       // Instructor: view all assignments
    ASSIGNMENTS_VIEW_PUBLISHED, // Student: view published assignments only
    ASSIGNMENTS_CREATE,
    ASSIGNMENTS_EDIT,           // Edit assignment (includes publish/unpublish)
    ASSIGNMENTS_DELETE,

    // ===== Submissions =====
    SUBMISSIONS_VIEW_ALL,       // Instructor: view all submissions in their sections
    SUBMISSIONS_VIEW_OWN,       // Student: view own submissions
    SUBMISSIONS_CREATE,         // Student: submit assignment
    SUBMISSIONS_GRADE,          // Instructor: grade submissions

    // ===== Profile Management =====
    PROFILE_VIEW_OWN,
    PROFILE_EDIT_OWN
}