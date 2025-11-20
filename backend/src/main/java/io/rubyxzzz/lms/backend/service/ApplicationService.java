//package io.rubyxzzz.lms.backend.service;
//
//import io.rubyxzzz.lms.backend.constants.BusinessConstants;
//import io.rubyxzzz.lms.backend.dto.listItem.ApplicationList;
//import io.rubyxzzz.lms.backend.dto.request.CreateApplicationReq;
//import io.rubyxzzz.lms.backend.dto.request.ReviewApplicationReq;
//import io.rubyxzzz.lms.backend.dto.response.ApplicationRes;
//import io.rubyxzzz.lms.backend.dto.response.StudentRes;
//import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
//import io.rubyxzzz.lms.backend.mapper.ApplicationMapper;
//import io.rubyxzzz.lms.backend.mapper.StudentMapper;
//import io.rubyxzzz.lms.backend.model.*;
//import io.rubyxzzz.lms.backend.repository.ApplicationRepo;
//import io.rubyxzzz.lms.backend.repository.StudentRepo;
//import io.rubyxzzz.lms.backend.util.IdGenerator;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class ApplicationService {
//
//    private final ApplicationRepo applicationRepo;
//    private final StudentRepo studentRepo;
//    private final ApplicationMapper applicationMapper;
//    private final StudentMapper studentMapper;
//    private final IdGenerator idGenerator;
//    // private final EmailService emailService;  // TODO: implement email service
//
//    /**
//     * Create a new application (public submission, no login required)
//     */
//    public ApplicationRes createApplication(CreateApplicationReq request) {
//
//        // ===== Deduplication Check 1: Already a student =====
//        if (studentRepo.existsByEmail(request.getEmail())) {
//            throw new IllegalArgumentException(
//                    "Email already registered as student. Please login to enroll in courses."
//            );
//        }
//
//        // ===== Deduplication Check 2: Active application for same session =====
//        Optional<Application> existingApp = applicationRepo
//                .findActiveApplicationByEmailAndSession(
//                        request.getEmail(),
//                        request.getTargetSessionCode()
//                );
//
//        if (existingApp.isPresent()) {
//            Application existing = existingApp.get();
//            String appId = existing.getApplicationId();
//
//            switch (existing.getApplicationStatus()) {
//                case SUBMITTED:
//                    throw new IllegalArgumentException(
//                            "You already have a pending application for this session. " +
//                                    "Application ID: " + appId
//                    );
//                case UNDER_REVIEW:
//                    throw new IllegalArgumentException(
//                            "Your application is under review. " +
//                                    "Application ID: " + appId
//                    );
//                case APPROVED:
//                    throw new IllegalArgumentException(
//                            "Your application has been approved. " +
//                                    "Please wait for account creation email."
//                    );
//                default:
//                    break;
//            }
//        }
//
//        // ===== Build Application Entity =====
//        Application application = new Application();
//        BeanUtils.copyProperties(request, application);
//
//        //  Set application-specific fields
//        application.setApplicationId(idGenerator.generateApplicationId());
//        application.setApplicationDate(LocalDate.now());
//        application.setApplicationStatus(ApplicationStatus.SUBMITTED);
//        application.setRegistrationType(RegistrationType.ONLINE);
//        application.setEmailVerified(false);
//        application.setPhoneVerified(false);
//        application.setApplicationFeePaid(false);
//        application.setConvertedToStudent(false);
//
//        // Calculate application fee based on student type
//        if (application.getStudentType() != null) {
//            Double fee = calculateApplicationFee(application.getStudentType());
//            application.setApplicationFee(fee);
//        }
//
//        // Save application
//        Application savedApplication = applicationRepo.save(application);
//
//        // TODO: Send confirmation email
//        // emailService.sendApplicationConfirmation(savedApplication);
//
//        return applicationMapper.toResponse(savedApplication);
//    }
//
//    /**
//     * Calculate application fee based on student type
//     */
//    private Double calculateApplicationFee(StudentType studentType) {
//        if (studentType == null) {
//            return 0.0;
//        }
//
//        switch (studentType) {
//            case F1_INITIAL:
//                return BusinessConstants.APPLICATION_FEE_F1_INITIAL;
//            case F1_TRANSFER:
//                return BusinessConstants.APPLICATION_FEE_F1_TRANSFER;
//            case CHANGE_OF_STATUS:
//                return BusinessConstants.APPLICATION_FEE_CHANGE_OF_STATUS;
//            case TOURIST:
//                return BusinessConstants.APPLICATION_FEE_TOURIST;
//            case LOCAL:
//                return BusinessConstants.APPLICATION_FEE_LOCAL;
//            default:
//                return 0.0;
//        }
//    }
//
//    /**
//     * Set application to under review
//     */
//    public ApplicationRes setUnderReview(String applicationUUID) {
//        Application application = applicationRepo.findByUUId(applicationUUID)
//                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationUUID));
//
//        if (application.getApplicationStatus() != ApplicationStatus.SUBMITTED) {
//            throw new IllegalStateException(
//                    "Can only set SUBMITTED applications to UNDER_REVIEW. Current: " +
//                            application.getApplicationStatus()
//            );
//        }
//
//        application.setApplicationStatus(ApplicationStatus.UNDER_REVIEW);
//        Application updatedApplication = applicationRepo.save(application);
//
//        return applicationMapper.toResponse(updatedApplication);
//    }
//
//    /**
//     * Review application (approve or reject)
//     */
//    public ApplicationRes reviewApplication(ReviewApplicationReq request) {
//        Application application = applicationRepo.findByUUId(request.getApplicationUUID())
//                .orElseThrow(() -> new ResourceNotFoundException("Application", request.getApplicationUUID()));
//
//        if (application.getApplicationStatus() != ApplicationStatus.SUBMITTED &&
//                application.getApplicationStatus() != ApplicationStatus.UNDER_REVIEW) {
//            throw new IllegalStateException(
//                    "Application cannot be reviewed in current status: " +
//                            application.getApplicationStatus()
//            );
//        }
//
//        application.setApplicationStatus(request.getStatus());
//        application.setReviewNotes(request.getReviewNotes());
//        application.setReviewedBy(request.getReviewedBy());
//        application.setReviewedDate(LocalDate.now());
//
//
//
//        Application updatedApplication = applicationRepo.save(application);
//
//        // TODO: Send notification email
//
//        return applicationMapper.toResponse(updatedApplication);
//    }
//
//    /**
//     * Convert approved application to student
//     */
//    public StudentRes convertToStudent(String applicationUUID, String approvedBy) {
//        Application application = applicationRepo.findByUUId(applicationUUID)
//                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationUUID));
//
//        if (application.getApplicationStatus() != ApplicationStatus.APPROVED) {
//            throw new IllegalStateException(
//                    "Only APPROVED applications can be converted. Current: " +
//                            application.getApplicationStatus()
//            );
//        }
//
//
//
//        if (studentRepo.existsByEmail(application.getEmail())) {
//            throw new IllegalStateException("Email already registered as student");
//        }
//
//        Student student = new Student();
//
//        student.setStudentId(idGenerator.generateStudentId());
//        student.setFirstName(application.getFirstName());
//        student.setLastName(application.getLastName());
//        student.setEmail(application.getEmail());
//        student.setPhone(application.getPhone());
//        student.setAddress(application.getAddress());
//        student.setDateOfBirth(application.getDateOfBirth());
//        student.setGender(application.getGender());
//
//        student.setEmergencyContact(application.getEmergencyContact());
//        student.setEmergencyPhone(application.getEmergencyPhone());
//        student.setGuardianName(application.getGuardianName());
//        student.setGuardianPhone(application.getGuardianPhone());
//        student.setGuardianEmail(application.getGuardianEmail());
//
//        student.setEmailVerified(application.getEmailVerified());
//        student.setPhoneVerified(application.getPhoneVerified());
//
//        student.setNativeLanguage(application.getNativeLanguage());
//        student.setTargetLanguage(application.getTargetLanguage());
//
//
//        student.setStudentType(application.getStudentType());
//        student.setRegistrationType(RegistrationType.ONLINE);
//        student.setSourceApplicationId(application.getApplicationId());
//        student.setApprovedBy(approvedBy);
//        student.setApprovedDate(LocalDate.now());
//        student.setStatus(UserStatus.ACTIVE);
//
//        student.setCurProgramCode(application.getTargetProgram());
//        student.setCurLevelNumber(1);
//        student.setTotalHoursEnrolled(0);
//        student.setEnrolledCounts(0);
//        student.setCompletedLevels(new ArrayList<>());
//
//        String tempPassword = generateTempPassword();
//        student.setPassword(tempPassword);
//        student.setCreatedAt(application.getCreatedAt());
//
//        Student savedStudent = studentRepo.save(student);
//
//        application.setApplicationStatus(ApplicationStatus.CONVERTED);
//
//        applicationRepo.save(application);
//
//        System.out.println("========================================");
//        System.out.println("WELCOME EMAIL (TODO: implement email service)");
//        System.out.println("To: " + savedStudent.getEmail());
//        System.out.println("Student ID: " + savedStudent.getStudentId());
//        System.out.println("Temporary Password: " + tempPassword);
//        System.out.println("========================================");
//
//        return studentMapper.toResponse(savedStudent);
//    }
//
//    private String generateTempPassword() {
//        return "Temp" + UUID.randomUUID().toString().substring(0, 4);
//    }
//
//    public ApplicationRes getApplication(String applicationUUID) {
//        Application application = applicationRepo.findByUUId(applicationUUID)
//                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationUUID));
//        return applicationMapper.toResponse(application);
//    }
//
//    public ApplicationRes getApplicationByApplicationId(String applicationId) {
//        Application application = applicationRepo.findByApplicationId(applicationId)
//                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
//        return applicationMapper.toResponse(application);
//    }
//
//    public ApplicationRes getApplicationByEmail(String email) {
//        Application application = applicationRepo.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("Application", "email", email));
//        return applicationMapper.toResponse(application);
//    }
//
//    public List<ApplicationRes> getAllApplications() {
//        return applicationMapper.toResponseList(applicationRepo.findAll());
//    }
//
//    public List<ApplicationRes> getPendingApplications() {
//        return applicationMapper.toResponseList(applicationRepo.findPendingApplications());
//    }
//
//    public List<ApplicationRes> getUnderReviewApplications() {
//        return applicationMapper.toResponseList(applicationRepo.findUnderReviewApplications());
//    }
//
//    public List<ApplicationRes> getApprovedApplications() {
//        return applicationMapper.toResponseList(applicationRepo.findApprovedApplications());
//    }
//
//    public List<ApplicationRes> getRejectedApplications() {
//        return applicationMapper.toResponseList(applicationRepo.findRejectedApplications());
//    }
//
//    public List<ApplicationRes> getConvertedApplications() {
//        return applicationMapper.toResponseList(applicationRepo.findConvertedApplications());
//    }
//
//    public List<ApplicationRes> getWithdrawnApplications() {
//        return applicationMapper.toResponseList(applicationRepo.findWithdrawnApplications());
//    }
//
//    public List<ApplicationRes> getApplicationsByStatus(ApplicationStatus status) {
//        return applicationMapper.toResponseList(applicationRepo.findByStatus(status));
//    }
//
//    public List<ApplicationRes> getApplicationsBySession(String targetSessionCode) {
//        return applicationMapper.toResponseList(applicationRepo.findByTargetSessionCode(targetSessionCode));
//    }
//
//    public List<ApplicationRes> getApplicationsByProgram(String targetProgram) {
//        return applicationMapper.toResponseList(applicationRepo.findByTargetProgram(targetProgram));
//    }
//
//    public List<ApplicationList> getApplicationsList() {
//        return applicationMapper.toListItems(applicationRepo.findAll());
//    }
//
//    public ApplicationRes withdrawApplication(String applicationUUID) {
//        Application application = applicationRepo.findByUUId(applicationUUID)
//                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationUUID));
//
//        if (application.getApplicationStatus() != ApplicationStatus.SUBMITTED &&
//                application.getApplicationStatus() != ApplicationStatus.UNDER_REVIEW) {
//            throw new IllegalStateException(
//                    "Cannot withdraw application in current status: " +
//                            application.getApplicationStatus()
//            );
//        }
//
//        application.setApplicationStatus(ApplicationStatus.WITHDRAWN);
//
//
//        Application updatedApplication = applicationRepo.save(application);
//        return applicationMapper.toResponse(updatedApplication);
//    }
//
//    public void deleteApplication(String applicationUUID) {
//        Application application = applicationRepo.findByUUId(applicationUUID)
//                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationUUID));
//
//        if (application.getApplicationStatus() != ApplicationStatus.WITHDRAWN &&
//                application.getApplicationStatus() != ApplicationStatus.REJECTED &&
//                application.getApplicationStatus() != ApplicationStatus.CONVERTED) {
//            throw new IllegalStateException(
//                    "Can only delete WITHDRAWN, REJECTED, or CONVERTED applications. Current: " +
//                            application.getApplicationStatus()
//            );
//        }
//
//        applicationRepo.delete(applicationUUID);
//    }
//}