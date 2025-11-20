package io.rubyxzzz.lms.backend.constants;

/**
 * Business Constants for Language School LMS
 * Contains all configurable business rules and limits
 */
public class BusinessConstants {
    // ===== Application Fees =====
    public static final double APPLICATION_FEE_F1_INITIAL = 250.0;
    public static final double APPLICATION_FEE_F1_TRANSFER = 150.0;
    public static final double APPLICATION_FEE_CHANGE_OF_STATUS = 400.0;
    public static final double APPLICATION_FEE_TOURIST = 150.0;
    public static final double APPLICATION_FEE_LOCAL = 150.0;

    public static final int PLACEMENT_SCORE_L1_MAX = 20;
    public static final int PLACEMENT_SCORE_L2_MAX = 40;
    public static final int PLACEMENT_SCORE_L3_MAX = 60;
    public static final int PLACEMENT_SCORE_L4_MAX = 75;
    public static final int PLACEMENT_SCORE_L5_MAX = 90;


    // ===== Enrollment Limits =====

    /**
     * Maximum number of courses a student can enroll in simultaneously
     */
    public static final int MAX_COURSE_ENROLLMENT = 3;


    // ===== Course Capacity =====

    /**
     * Minimum enrollment required to start a course
     * Course will be cancelled if not met
     */
    public static final int MIN_COURSE_ENROLLMENT = 5;

    /**
     * Maximum capacity per course
     */
    public static final int MAX_COURSE_CAPACITY = 40;

    /**
     * Default course capacity if not specified
     */
    public static final int DEFAULT_COURSE_CAPACITY = 30;

    // ===== Academic Requirements =====

    /**
     * Passing grade threshold (out of 100)
     */
    public static final double PASSING_GRADE = 80.0;

    /**
     * Minimum attendance rate required for certificate (percentage)
     */
    public static final double MIN_ATTENDANCE_RATE = 80.0;

    /**
     * Minimum assignment completion rate required for certificate (percentage)
     */
    public static final double MIN_ASSIGNMENT_COMPLETION_RATE = 80.0;

    /**
     * Grade required to progress to next level (out of 100)
     */
    public static final double LEVEL_PROGRESSION_GRADE = 80.0;

    // ===== Session Configuration =====

    /**
     * Number of sessions per year
     */
    public static final int SESSIONS_PER_YEAR = 6;

    /**
     * Duration of each session in weeks
     */
    public static final int SESSION_DURATION_WEEKS = 8;

    // ===== Payment =====

    /**
     * Payment deadline in hours after enrollment
     */
    public static final int PAYMENT_DEADLINE_DAYS = 7;

    /**
     * Default currency
     */
    public static final String DEFAULT_CURRENCY = "USD";


    // ===== Application Processing =====

    /**
     * Maximum days to process an application before auto-reminder
     */
    public static final int APPLICATION_REVIEW_DEADLINE_DAYS = 7;

    // ===== ID Format Configuration =====

    /**
     * Student ID format: S{YEAR}{SEQUENCE}
     * Example: S202500001
     */
    public static final String STUDENT_ID_PREFIX = "S";

    /**
     * Employee ID format: E{YEAR}{SEQUENCE}
     * Example: E202500001
     */
    public static final String EMPLOYEE_ID_PREFIX = "E";

    /**
     * Application ID format: A{YEAR}{SEQUENCE}
     * Example: A202500001
     */
    public static final String APPLICATION_ID_PREFIX = "A";

    /**
     * Sequence number length (5 digits = 00001-99999)
     */
    public static final int ID_SEQUENCE_LENGTH = 5;


    // ===== Hour Requirements (F-1 Compliance) =====

    /**
     * Minimum hours per week for full-time students (F-1 requirement)
     */
    public static final int FULLTIME_MIN_HOURS = 18;

    /**
     * Maximum hours per week for tourist visa students
     */
    public static final int TOURIST_MAX_HOURS = 17;

    /**
     * Hours per course (standard)
     */
    public static final int HOURS_PER_COURSE = 9;

    /**
     * Courses per level (standard: LS + RW)
     */
    public static final int COURSES_PER_LEVEL = 2;

// ===== Tuition Pricing =====

    /**
     * Package price per session (full-time: 2 courses)
     */
    public static final double PACKAGE_PRICE_PER_SESSION = 1500.0;

    /**
     * LS course price per session (part-time)
     */
    public static final double LS_COURSE_PRICE_PER_SESSION = 800.0;

    /**
     * RW course price per session (part-time)
     */
    public static final double RW_COURSE_PRICE_PER_SESSION = 750.0;

    /**
     * Multi-session discount - 2 sessions
     */
    public static final double DISCOUNT_2_SESSIONS = 0.05;  // 5% off

    /**
     * Multi-session discount - 3+ sessions
     */
    public static final double DISCOUNT_3_SESSIONS = 0.10;  // 10% off


    // ===== Grading Scale =====

    /**
     * Letter grade A threshold (≥90)
     */
    public static final double GRADE_A_THRESHOLD = 90.0;

    /**
     * Letter grade B threshold (≥80)
     */
    public static final double GRADE_B_THRESHOLD = 80.0;

    /**
     * Letter grade C threshold (≥70)
     */
    public static final double GRADE_C_THRESHOLD = 70.0;

    /**
     * Letter grade D threshold (≥60)
     */
    public static final double GRADE_D_THRESHOLD = 60.0;

// Below 60 = F

    // Private constructor to prevent instantiation
    private BusinessConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
