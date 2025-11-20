package io.rubyxzzz.lms.backend.model;

public enum SubmissionStatus {
    MISSING,
    SUBMITTED,   // Student submitted, waiting for grading
    GRADED,      // Instructor graded
    LATE,         // Submitted after due date
}
