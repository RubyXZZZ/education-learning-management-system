package io.rubyxzzz.lms.backend.model;


import io.rubyxzzz.lms.backend.constants.BusinessConstants;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Application Entity for online applications
 * Represents a student application (independent from User system)
 *
 * Applicants DO NOT have login access to LMS
 * After approval, admin converts Application to Student account
 */


public class Application extends BaseEntity {

    // ===== Identity =====
    private String applicationId;  // A202500001

    // ===== Personal Information (from User) =====
    private String firstName;
    private String lastName;
    private String email;  // No password - cannot login
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;

    // ===== Contact Verification =====

    private Boolean emailVerified = false;

    private Boolean phoneVerified = false;

    // ===== Emergency Contact (optional for minors) =====
    private String emergencyContact;
    private String emergencyPhone;
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;

    // ===== Application Details =====
    private LocalDate applicationDate;
    private StudentType studentType;              // F1_INITIAL, F1_TRANSFER, CHANGE_OF_STATUS, TOURIST, LOCAL
//    private RegistrationType registrationType;    // Always ONLINE for Application
    private String targetProgram;
    private String targetSessionCode;          // Which session applying for
    private String eduLevel;             // Educational background
    private String languageBG;           // Language background
    private String nativeLanguage;
    private String targetLanguage;
    private String currentOccupation;
    private String languageTestScore;   // e.g., TOEFL/IELTS score

    //Uploaded document URLs (stored in cloud storage)
    private String passportUrl;                   // Passport scan
    private String visaDocumentUrl;               // Visa document (F-1/Tourist)
    private String transcriptUrl;                 // Transcript (if applicable)
    private String financialProofUrl;             // Financial proof (F-1 Initial)
    private String i20DocumentUrl;                // Current I-20 (F-1 Transfer)

    // ===== Application Fee =====
    private Double applicationFee;

    private Boolean applicationFeePaid = false;
    private LocalDate applicationFeePaidDate;
    private String applicationFeeTransactionId;

    // ===== Review Process =====

//    private ApplicationStatus applicationStatus = ApplicationStatus.SUBMITTED;
    private String reviewNotes;
    private String reviewedBy;           // Admin who reviewed
    private LocalDate reviewedDate;
    private String rejectionReason;

    //    private UserStatus status;                    // PENDING, ACTIVE, INACTIVE, REJECTED
    // ===== Tracking =====
    private Boolean convertedToStudent;
    private String convertedStudentId;            // Student ID after conversion (S202500001)
    private LocalDate convertedDate;

    // ===== Business Methods =====

    /**
     * Get full name
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Calculate age from date of birth
     */
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Check if applicant is a minor (under 18)
     */
    public boolean isMinor() {
        Integer age = getAge();
        return age != null && age < 18;
    }

    // ===== APPLICATION STATUS METHODS =====

//    /**
//     * Check if application is approved
//     */
//    public boolean isApproved() {
//        return this.applicationStatus == ApplicationStatus.APPROVED;
//    }
//
//    /**
//     * Check if application is pending review
//     */
//    public boolean isPending() {
//        return this.applicationStatus == ApplicationStatus.SUBMITTED ||
//                this.applicationStatus == ApplicationStatus.UNDER_REVIEW;
//    }

//    /**
//     * Check if user status is active
//     */
//    public boolean isActive() {
//        return this.status == UserStatus.ACTIVE;
//    }

    /**
     * Check if can be converted to student
     * Requirements:
     * 1. Application approved
     * 2. Application fee paid
     * 3. Not already converted
     * 4. User status is active
     *
     * Note: Placement test happens AFTER conversion to Student
     */
//    public boolean canConvertToStudent() {
//        return this.applicationStatus == ApplicationStatus.APPROVED &&
//                Boolean.TRUE.equals(this.applicationFeePaid) &&
//                !Boolean.TRUE.equals(this.convertedToStudent) ;
//    }



    // ===== STUDENT TYPE METHODS =====

    /**
     * Check if applicant is F-1 student type
     */
//    public boolean isF1Application() {
//        return studentType == StudentType.F1_INITIAL ||
//                studentType == StudentType.F1_TRANSFER ||
//                studentType == StudentType.CHANGE_OF_STATUS;
//    }
//
//    /**
//     * Check if needs I-20 document
//     */
//    public boolean needsI20() {
//        return studentType == StudentType.F1_INITIAL;
//    }

    // ===== CONVERSION METHODS =====

    /**
     * Mark as converted to student
     */
    public void markAsConverted(String studentId) {
        this.convertedToStudent = true;
        this.convertedStudentId = studentId;
        this.convertedDate = LocalDate.now();
//        this.applicationStatus = ApplicationStatus.CONVERTED;
    }

}
