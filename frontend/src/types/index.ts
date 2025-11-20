
// User Types (Base)
export type UserRole = 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';
export type UserStatus = 'PENDING' | 'ACTIVE' | 'SUSPENDED' | 'INACTIVE';
export type StudentType = 'FULL_TIME_ONLY' | 'PART_TIME_ONLY' | 'FLEXIBLE';
export type EnrollmentMode = 'FULL_TIME' | 'PART_TIME' | 'AUDITOR';
export type EnrollmentStatus = 'ENROLLED' | 'DROPPED' | 'WAITLISTED' | 'COMPLETED' | 'CANCELED' | 'WITHDRAWN' | 'PENDING';
export type SessionStatus = 'UPCOMING' | 'ACTIVE' | 'COMPLETED';
export type CourseSectionStatus = 'DRAFT' | 'PUBLISHED' | 'COMPLETED' | 'CANCELLED';
export type CourseFormat = 'IN_PERSON' | 'ONLINE' | 'HYBRID';
export type AssignmentType = 'ASSIGNMENT' | 'QUIZ' | 'DISCUSSION' | 'NOT_GRADED';
export type SubmissionStatus = 'MISSING' | 'SUBMITTED' | 'GRADED' | 'LATE';
export type SubmissionType = 'NO_SUBMISSION' | 'ONLINE_TEXT' | 'ONLINE_FILE' | 'ONLINE_URL';

export type ViewType =
    | 'dashboard'
    | 'my-courses'
    | 'course-registration'
    | 'users-management'
    | 'courses-management'
    | 'course-enrollments'
    | 'system-settings'
    | 'profile';

// enum

export const USER_STATUS = {
    PENDING: { value: 'PENDING', label: 'Pending', color: 'bg-yellow-100 text-yellow-700' },
    ACTIVE: { value: 'ACTIVE', label: 'Active', color: 'bg-green-100 text-green-700' },
    INACTIVE: { value: 'INACTIVE', label: 'Inactive', color: 'bg-gray-300 text-gray-600' },
    SUSPENDED: { value: 'SUSPENDED', label: 'Suspended', color: 'bg-red-100 text-red-700' }
} as const;

export const STUDENT_TYPE = {
    FULL_TIME_ONLY: { value: 'FULL_TIME_ONLY', label: 'Full-time Only (F-1)' },
    PART_TIME_ONLY: { value: 'PART_TIME_ONLY', label: 'Part-time Only (Tourist)' },
    FLEXIBLE: { value: 'FLEXIBLE', label: 'Flexible (Citizen/PR)' }
} as const;

export const SESSION_STATUS = {
    UPCOMING: { value: 'UPCOMING', label: 'Upcoming', color: 'bg-blue-100 text-blue-700' },
    ACTIVE: { value: 'ACTIVE', label: 'Active', color: 'bg-green-100 text-green-700' },
    COMPLETED: { value: 'COMPLETED', label: 'Completed', color: 'bg-gray-300 text-gray-600' }
} as const;

export const SECTION_STATUS = {
    DRAFT: { value: 'DRAFT', label: 'Draft', color: 'bg-gray-300 text-gray-600' },
    PUBLISHED: { value: 'PUBLISHED', label: 'Published', color: 'bg-green-100 text-green-700' },
    COMPLETED: { value: 'COMPLETED', label: 'Completed', color: 'bg-blue-100 text-blue-700' },
    CANCELLED: { value: 'CANCELLED', label: 'Cancelled', color: 'bg-red-100 text-red-700' }
} as const;

export const COURSE_FORMAT = {
    IN_PERSON: { value: 'IN_PERSON', label: 'In-Person', color: 'bg-green-100 text-green-700' },
    ONLINE: { value: 'ONLINE', label: 'Online', color: 'bg-blue-100 text-blue-700' },
    HYBRID: { value: 'HYBRID', label: 'Hybrid', color: 'bg-purple-100 text-purple-700' }
} as const;

export const ASSIGNMENT_TYPE = {
    ASSIGNMENT: { value: 'ASSIGNMENT', label: 'Assignment', color: 'bg-blue-100 text-blue-700' },
    QUIZ: { value: 'QUIZ', label: 'Quiz', color: 'bg-yellow-100 text-yellow-700' },
    DISCUSSION: { value: 'DISCUSSION', label: 'Discussion', color: 'bg-green-100 text-green-700' },
    NOT_GRADED: { value: 'NOT_GRADED', label: 'Not Graded', color: 'bg-gray-300 text-gray-600' }
} as const;

export const ENROLLMENT_STATUS = {
    ENROLLED: { value: 'ENROLLED', label: 'Enrolled', color: 'bg-blue-100 text-blue-700' },
    DROPPED: { value: 'DROPPED', label: 'Dropped', color: 'bg-gray-300 text-gray-600' },
    // WAITLISTED: { value: 'WAITLISTED', label: 'Waitlisted', color: 'bg-yellow-100 text-yellow-700' },
    COMPLETED: { value: 'COMPLETED', label: 'Completed', color: 'bg-green-100 text-green-700' },
    // CANCELED: { value: 'CANCELED', label: 'Canceled', color: 'bg-red-100 text-red-700' },
    // WITHDRAWN: { value: 'WITHDRAWN', label: 'Withdrawn', color: 'bg-gray-300 text-gray-600' },
    // PENDING: { value: 'PENDING', label: 'Pending', color: 'bg-yellow-100 text-yellow-700' }
} as const;

export const SUBMISSION_STATUS = {
    MISSING: { value: 'MISSING', label: 'Missing', color: 'bg-red-100 text-red-700' },
    SUBMITTED: { value: 'SUBMITTED', label: 'Submitted', color: 'bg-blue-100 text-blue-700' },
    GRADED: { value: 'GRADED', label: 'Graded', color: 'bg-green-100 text-green-700' },
    LATE: { value: 'LATE', label: 'Late', color: 'bg-yellow-100 text-yellow-700' }
} as const;

export const SUBMISSION_TYPE = {
    NO_SUBMISSION: { value: 'NO_SUBMISSION', label: 'No Submission' },
    ONLINE_TEXT: { value: 'ONLINE_TEXT', label: 'Online Text' },
    ONLINE_FILE: { value: 'ONLINE_FILE', label: 'File Upload' },
    ONLINE_URL: { value: 'ONLINE_URL', label: 'URL Submission' }
} as const;


export const DEPARTMENTS = [
    'Academic Department',
    'IT',
    'Administration'
] as const;


// Auth Types
export interface LoginRes {
    userId: string;
    email: string;
    fullName: string;
    userNumber: string;
    role: UserRole;
    status: UserStatus;
    userAvatar: string;
}

export interface UpdateProfileReq {
    firstName?: string;
    lastName?: string;
    phone?: string;
    address?: string;
    emergencyContact?: string;
    emergencyPhone?: string;
}

// Student Types
export interface StudentList {
    id: string;
    studentNumber: string;
    fullName: string;
    email: string;
    status: UserStatus;
    studentType: StudentType;
    // curLevelNumber: number | null;
    placementLevel: number | null;
    enrolledCounts: number;
    totalHoursEnrolled: number;
    gpa: number | null;
}

export interface StudentRes {
    id: string;
    studentNumber: string;
    firstName: string;
    lastName: string;
    fullName: string;
    email: string;
    phone: string;
    address: string;
    dateOfBirth: string;
    age: number;
    gender: string;
    userAvatar: string;
    status: UserStatus;
    nationality: string;
    emailVerified: boolean;
    emergencyContact: string;
    emergencyPhone: string;
    studentType: StudentType;
    // maxHoursAllowed: number;
    // minHoursRequired: number;
    placementLevel: number | null;
    placementTestDate: string | null;
    enrolledCounts: number;
    totalHoursEnrolled: number;
    enrollmentMode: EnrollmentMode;
    canEnrollMore: boolean;
    canEnrollCourse: boolean;
    // gpa: number | null;
    totalCoursesCompleted: number;
    // totalCoursesPassed: number;
    // totalCoursesFailed: number;
    // passRate: number;
    enrollments?: EnrollmentRes[];
    createdAt: string;
    updatedAt: string;
}

// Instructor Types
export interface InstructorList {
    id: string;
    employeeNumber: string;
    fullName: string;
    email: string;
    department: string;
    teachingCounts: number;
    status: UserStatus;
}

export interface InstructorRes {
    id: string;
    employeeNumber: string;
    firstName: string;
    lastName: string;
    fullName: string;
    email: string;
    phone: string;
    address: string;
    dateOfBirth: string;
    age: number;
    gender: string;
    userAvatar: string;
    emailVerified: boolean;
    department: string;
    officeHours: string;
    teachingCounts: number;
    status: UserStatus;
    sections?: SectionRes[];
    createdAt: string;
    updatedAt: string;
}

// Admin Types
export interface AdminList {
    id: string;
    employeeNumber: string;
    fullName: string;
    email: string;
    department: string;
    position: string;
    isSuperAdmin: boolean;
    status: UserStatus;
}

export interface AdminRes {
    id: string;
    employeeNumber: string;
    firstName: string;
    lastName: string;
    fullName: string;
    email: string;
    emailVerified: boolean;
    phone: string;
    userAvatar: string;
    status: UserStatus;
    address: string;
    dateOfBirth: string;
    gender: string;
    department: string;
    position: string;
    officeHours: string;
    isSuperAdmin: boolean;
    createdAt: string;
    updatedAt: string;
}


export interface CourseRes {
    id: string;
    courseCode: string;
    courseName: string;
    courseDescription: string;
    sessionId: string;
    sessionCode: string;
    prerequisiteCourses: string[];
    requiredPlacementLevel: number | null;
    allowHigherPlacement: boolean;
    prerequisiteDisplay: string;
    isRequired: boolean;
    hoursPerWeek: number;
    isActive: boolean;
    sections?: SectionRes[];
    sectionsCount?: number;
    createdAt: string;
    updatedAt: string;
}

// Section Types
export interface CourseSectionList {
    id: string;
    courseCode: string;
    sectionCode: string;
    sessionCode: string;
    instructorName: string;
    schedule: string;
    location: string;
    enrolledCount: number;
    capacity: number;
    availableSeats: number;
    status: CourseSectionStatus;
    openForEnrollment: boolean;
}

// Section Types
export interface CourseSectionList {
    id: string;
    sectionCode: string;
    courseId: string;
    courseCode: string;
    courseName: string;
    hoursPerWeek: number;
    sessionCode: string;
    courseFormat: CourseFormat;
    schedule: string;
    location: string;
    instructorId: string;
    instructorName: string;
    capacity: number;
    enrolledCount: number;
    availableSeats: number;
    status: CourseSectionStatus;
    enrollmentLocked: boolean;
    openForEnrollment: boolean;
}

export interface SectionRes {
    id: string;
    sectionCode: string;
    courseId: string;
    courseCode: string;
    courseName: string;
    hoursPerWeek: number;
    courseDescription: string;
    prerequisiteCourses: string[];
    requiredPlacementLevel: number;
    allowHigherPlacement: boolean;
    prerequisiteDisplay: string;
    sessionCode: string;
    courseFormat: CourseFormat;
    schedule: string;
    daysOfWeek: string;
    startTime: string;
    endTime: string;
    location: string;
    instructorId: string;
    instructorNumber: string;
    instructorName: string;
    instructorEmail: string;
    capacity: number;
    minEnrollment: number;
    enrolledCount: number;
    availableSeats: number;
    capacityUtilization: number;
    status: CourseSectionStatus;
    enrollmentLocked: boolean;
    openForEnrollment: boolean;
    meetsMinimumEnrollment: boolean;
    averageGrade: number | null;
    completionRate: number | null;
    createdAt: string;
    updatedAt: string;
}

// Enrollment Types
export interface EnrollmentList {
    id: string;
    studentNumber: string;
    studentName: string;
    studentEmail: string;
    sectionCode: string;
    courseCode: string;
    courseName: string;
    sessionCode: string;
    // enrollmentMode: EnrollmentMode;
    hoursPerWeek: number;
    enrolledTime: string;
    status: EnrollmentStatus;
    // progress: number | null;
    finalGrade: number | null;
    // letterGrade: string | null;
    // passed: boolean | null;
    // attendanceRate: number;
}

export interface EnrollmentRes {
    id: string;
    studentId: string;
    studentNumber: string;
    studentName: string;
    studentEmail: string;
    studentAvatar: string; // todo
    courseSectionId: string;
    sectionCode: string;
    courseId: string;
    courseCode: string;
    courseName: string;
    sessionCode: string;
    // enrollmentMode: EnrollmentMode;
    hoursPerWeek: number;
    enrolledTime: string;
    createdBy: string;
    droppedTime: string | null;
    droppedBy: string | null;
    status: EnrollmentStatus;
    finalGrade: number | null;
    // letterGrade: string | null;
    // passed: boolean | null;
    // attendanceRate: number;
    // totalClasses: number;
    // attendedClasses: number;
    // absentClasses: number;
    lateClasses: number;
    instructorId: string;
    instructorName: string;
    instructorEmail: string;
    dropReason: string | null;
    createdAt: string;
    updatedAt: string;
    updatedBy: string;
}

// Session Types
export interface SessionRes {
    id: string;
    sessionCode: string;
    startDate: string;
    endDate: string;
    status: SessionStatus;
    totalCoursesOffered: number;
    totalEnrollments: number;
    active: boolean;
    // registrationOpen: boolean;
    courses?: CourseRes[];
}

// Module Types
export interface ModuleRes {
    id: string;
    name: string;
    description: string;
    courseSectionId: string;
    orderNum: number;
    isPublished: boolean;
    createdAt: string;
    updatedAt: string;
}

// CoursePage Types
export interface CoursePageRes {
    id: string;
    title: string;
    body: string;  // HTML content
    courseSectionId: string;
    moduleId: string | null;
    moduleName: string | null;
    isPublished: boolean;
    createdAt: string;
    updatedAt: string;
}

// Assignment Types
export interface AssignmentRes {
    id: string;
    title: string;
    content: string;
    courseSectionId: string;
    assignmentType: AssignmentType;
    submissionType: SubmissionType;
    totalPoints: number;
    dueDate: string | null;
    timeLimit: number | null;
    showCorrectAnswers: boolean;
    isPublished: boolean;
    isOverdue: boolean;
    acceptsSubmissions: boolean;
    submissionCount: number;
    gradedCount: number;
    maxAttempts: number;
    createdAt: string;
    updatedAt: string;
}

// Submission Types
export interface SubmissionRes {
    id: string;
    assignmentId: string;
    assignmentTitle: string;
    totalPoints: number;
    dueDate: string | null;
    studentId: string;
    studentNumber: string;
    studentName: string;
    studentEmail: string;
    content: string | null;
    fileUrl: string | null;
    externalUrl: string | null;
    status: SubmissionStatus;
    isLate: boolean;
    grade: number | null;
    feedback: string | null;
    gradedAt: string | null;
    gradedBy: string | null;
    gradedByName: string | null;
    attemptNumber: number;
    isLatest: boolean;
    submittedAt: string | null;
    createdAt: string;
    updatedAt: string;
}




