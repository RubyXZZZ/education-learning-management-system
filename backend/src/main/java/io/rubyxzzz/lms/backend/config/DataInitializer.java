package io.rubyxzzz.lms.backend.config;//package io.rubyxzzz.lms.backend.config;
//
//import io.rubyxzzz.lms.backend.model.*;
//import io.rubyxzzz.lms.backend.repository.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//


///**
// * Initialize demo data for development/testing
// * Run once on application startup
// */



//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements ApplicationRunner {
//
//    private final SessionRepo sessionRepository;
//    private final StudentRepo studentRepository;
//    private final InstructorRepo instructorRepository;
//    private final AdminRepo adminRepository;
//    private final CourseRepo courseRepository;
//    private final EnrollmentRepo enrollmentRepository;
//    private final ApplicationRepo applicationRepository;
//

//    @Override
//    public void run(ApplicationArguments args) {
//        // Only initialize if database is empty
//        if (sessionRepository.count() > 0) {
//            log.info("Database already initialized, skipping data initialization");
//            return;
//        }
//
//        log.info("Starting data initialization...");
//
//        try {
//            // 1. Create Sessions (2025)
//            createSessions();
//
//            // 2. Create Admins
//            createAdmins();
//
//            // 3. Create Instructors
//            createInstructors();
//
//            // 4. Create Students
//            createStudents();
//
//            // 5. Create Courses
//            createCourses();
//
//            // 6. Create Enrollments
//            createEnrollments();
//
//            // 7. Create Applications
//            createApplications();
//
//            log.info("Data initialization completed successfully!");
//        } catch (Exception e) {
//            log.error("Error during data initialization", e);
//        }
//    }
//

//    private void createSessions() {
//        log.info("Creating sessions...");
//
//        Session[] sessions = {
//                createSession(1, 2025, LocalDate.of(2025, 1, 6), LocalDate.of(2025, 2, 28)),
//                createSession(2, 2025, LocalDate.of(2025, 3, 3), LocalDate.of(2025, 4, 25)),
//                createSession(3, 2025, LocalDate.of(2025, 4, 28), LocalDate.of(2025, 6, 20)),
//                createSession(4, 2025, LocalDate.of(2025, 6, 23), LocalDate.of(2025, 8, 15)),
//                createSession(5, 2025, LocalDate.of(2025, 8, 18), LocalDate.of(2025, 10, 10)),
//                createSession(6, 2025, LocalDate.of(2025, 10, 13), LocalDate.of(2025, 12, 5)),
//
//                createSession(1, 2026, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 27)),
//                createSession(2, 2026, LocalDate.of(2026, 3, 2), LocalDate.of(2026, 4, 24))
//        };
//
//        for (Session session : sessions) {
//            sessionRepository.save(session);
//        }
//
//        log.info("Created {} sessions", sessions.length);
//    }
//
//    private Session createSession(int sessionNumber, int year, LocalDate startDate, LocalDate endDate) {
//        Session session = new Session();
//        session.setSessionNumber(sessionNumber);
//        session.setYear(year);
//        session.setStartDate(startDate);
//        session.setEndDate(endDate);
//        session.setWeeks(8);
//
//        // Set registration dates
//        session.setRegistrationOpenDate(startDate.minusWeeks(4));
//        session.setRegistrationDeadline(startDate.minusWeeks(1));
//        session.setPaymentDeadline(startDate.plusDays(3));
//        session.setAddDropDeadline(startDate.plusWeeks(2));
//        session.setWithdrawDeadline(startDate.plusWeeks(6));
//
//        // Set status based on current date
//        LocalDate now = LocalDate.now();
//        if (now.isBefore(startDate)) {
//            session.setStatus(SessionStatus.UPCOMING);
//        } else if (now.isAfter(endDate)) {
//            session.setStatus(SessionStatus.COMPLETED);
//        } else {
//            session.setStatus(SessionStatus.ACTIVE);
//        }
//
//        return session;
//    }
//
//    private void createAdmins() {
//        log.info("Creating admins...");
//
//        // Super Admin
//        Admin superAdmin = new Admin();
//        superAdmin.setFirstName("Ruby");
//        superAdmin.setLastName("Zhang");
//        superAdmin.setEmail("admin@school.com");
//        superAdmin.setPassword("admin123"); // TODO: Hash password
//        superAdmin.setPhone("555-0001");
//        superAdmin.setEmployeeId("E202500001");
//        superAdmin.setDepartment("Administration");
//        superAdmin.setPosition("Super Administrator");
//        superAdmin.setIsSuperAdmin(true);
//        superAdmin.setStatus(UserStatus.ACTIVE);
//        adminRepository.save(superAdmin);
//
//        log.info("Created 1 admin");
//    }
//
//    private void createInstructors() {
//        log.info("Creating instructors...");
//
//        Instructor[] instructors = {
//                createInstructor("Yang", "Li", "li@school.com", "E202500101", "Language"),
//                createInstructor("John", "Smith", "smith@school.com", "E202500102", "Language"),
//                createInstructor("Eric", "Garcia", "garcia@school.com", "E202500103", "Language"),
//                createInstructor("Amber", "Dubois", "dubois@school.com", "E202500104", "Language")
//        };
//
//        for (Instructor instructor : instructors) {
//            instructorRepository.save(instructor);
//        }
//
//        log.info("Created {} instructors", instructors.length);
//    }
//
//    private Instructor createInstructor(String firstName, String lastName, String email,
//                                        String employeeId, String department) {
//        Instructor instructor = new Instructor();
//        instructor.setFirstName(firstName);
//        instructor.setLastName(lastName);
//        instructor.setEmail(email);
//        instructor.setPassword("instructor123");
//        instructor.setPhone("555-010" + employeeId.substring(employeeId.length() - 1));
//        instructor.setEmployeeId(employeeId);
//        instructor.setDepartment(department);
//        instructor.setOfficeHours("Mon-Fri 2:00-4:00 PM");
//        instructor.setStatus(UserStatus.ACTIVE);
//        return instructor;
//    }
//
//    private void createStudents() {
//        log.info("Creating students...");
//
//        Student[] students = {
//                createStudent("Amanda", "Chen", "amanda@school.com", "S202500001", CEFRLevel.B1),
//                createStudent("John", "Smith", "john@school.com", "S202500002", CEFRLevel.A2),
//                createStudent("Maria", "Rodriguez", "maria@school.com", "S202500003", CEFRLevel.B2),
//                createStudent("Wei", "Zhang", "wei@school.com", "S202500004", CEFRLevel.A1)
//        };
//
//        for (Student student : students) {
//            studentRepository.save(student);
//        }
//
//        log.info("Created {} students", students.length);
//    }
//
//    private Student createStudent(String firstName, String lastName, String email,
//                                  String studentId, CEFRLevel currentLevel) {
//        Student student = new Student();
//        student.setFirstName(firstName);
//        student.setLastName(lastName);
//        student.setEmail(email);
//        student.setPassword("student123"); // TODO: Hash password
//        student.setPhone("555-020" + studentId.substring(studentId.length() - 1));
//        student.setStudentId(studentId);
//        student.setStudentType(StudentType.LOCAL);
//        student.setRegistrationType(RegistrationType.WALK_IN);
//        student.setCurProgramCode("ESL");
//        student.setCurLevelNumber(3);
//        student.setCurCEFRLevel(currentLevel);
//        student.setNativeLanguage("English");
//        student.setTargetLanguage("Chinese");
//        student.setStatus(UserStatus.ACTIVE);
//        student.setDateOfBirth(LocalDate.of(2000, 1, 1));
//
//        return student;
//    }
//
//    private void createCourses() {
//        log.info("Creating courses...");
//
//        // Get current session (should be 2025-S5 based on October date)
//        Session currentSession = sessionRepository.findByYearAndNumber(2025, 5)
//                .orElseThrow(() -> new RuntimeException("Session 2025-S5 not found"));
//
//        // Get instructors
//        Instructor li = instructorRepository.findByEmail("li@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Li not found"));
//        Instructor smith = instructorRepository.findByEmail("smith@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Smith not found"));
//        Instructor garcia = instructorRepository.findByEmail("garcia@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Garcia not found"));
//        Instructor dubois = instructorRepository.findByEmail("dubois@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Dubois not found"));
//
//        Course[] courses = {
//                createCourse("GE-101", "Chinese 101", "CHINESE", CEFRLevel.A1,
//                        currentSession, li, "Mon, Wed 2:00-3:30 PM", "Room 201"),
//                createCourse("GE-102", "English Advanced", "ENGLISH", CEFRLevel.B2,
//                        currentSession, smith, "Tue, Thu 10:00-11:30 AM", "Room 305"),
//                createCourse("GE-103", "Spanish Basics", "SPANISH", CEFRLevel.A1,
//                        currentSession, garcia, "Mon, Wed, Fri 3:00-4:00 PM", "Room 302"),
//                createCourse("GE-104", "French Conversation", "FRENCH", CEFRLevel.B1,
//                        currentSession, dubois, "Thu 4:00-5:30 PM", "Online")
//        };
//
//        for (Course course : courses) {
//            courseRepository.save(course);
//        }
//
//        log.info("Created {} courses", courses.length);
//    }
//
//    private Course createCourse(String courseCode, String courseName, String language,
//                                CEFRLevel cefrLevel, Session session, Instructor instructor,
//                                String schedule, String location) {
//        Course course = new Course();
//        course.setCourseCode(courseCode);
//        course.setCourseName(courseName);
//        course.setCourseDescription("Introduction to " + language);
//        course.setProgramCode("ESL");
//        course.setLevelNumber(1);
//        course.setCourseLevelName("L1");
//        course.setMinCEFRLevel(null);
//        course.setTargetCEFRLevel(cefrLevel);
//        course.setIsRequired(true);
//        course.setHoursPerWeek(9);
//
//        course.setLanguage(language);
//
//        // Session info
//        course.setSessionNumber(session.getSessionNumber());
//        course.setYear(session.getYear());
//        course.setStartDate(session.getStartDate());
//        course.setEndDate(session.getEndDate());
//        course.setWeeks(8);
//
//        // Schedule
//        course.setSchedule(schedule);
//        course.setLocation(location);
//        course.setCourseFormat(location.equals("Online") ? CourseFormat.ONLINE : CourseFormat.IN_PERSON);
//
//        // Instructor
//        course.setInstructorUUID(instructor.getId());
//        course.setInstructorId(instructor.getEmployeeId());
//        course.setInstructorName(instructor.getFullName());
//        course.setInstructorEmail(instructor.getEmail());
//
//        // Capacity
//        course.setCapacity(30);
//        course.setMinEnrollment(5);
//
//        // Pricing
//        course.setTuition(960.0);
//        course.setCurrency("USD");
//
//        // Requirements
//        course.setPassingGrade(80.0);
//        course.setAttendanceRequirement(80.0);
//        course.setAssignmentRequirement(80.0);
//
//        // Status
//        course.setStatus(CourseStatus.ENROLL_OPEN);
//
//        return course;
//    }
//
//    private void createEnrollments() {
//        log.info("Creating enrollments...");
//
//        // Get student Amanda Chen
//        Student amanda = studentRepository.findByStudentId("S202500001")
//                .orElseThrow(() -> new RuntimeException("Student Amanda not found"));
//
//        // Get course Chinese 101
//        Course chinese101 = courseRepository.findByCourseCode("GE-101")
//                .orElseThrow(() -> new RuntimeException("Course Chinese 101 not found"));
//
//        // Create enrollment
//        Enrollment enrollment = new Enrollment();
//
//        // UUID relations
//        enrollment.setStudentUUID(amanda.getId());
//        enrollment.setCourseUUID(chinese101.getId());
//
//        // Display IDs
//        enrollment.setStudentId(amanda.getStudentId());
//        enrollment.setStudentName(amanda.getFullName());
//        enrollment.setCourseCode(chinese101.getCourseCode());
//        enrollment.setCourseName(chinese101.getCourseName());
//
//        // Session
//        enrollment.setSessionNumber(chinese101.getSessionNumber());
//        enrollment.setYear(chinese101.getYear());
//        enrollment.setSessionCode(chinese101.getYear() + "-S" + chinese101.getSessionNumber());
//
//        // Timeline
//        enrollment.setEnrolledTime(LocalDateTime.now());
//        enrollment.setStatus(EnrollmentStatus.ENROLLED);
//        enrollment.setProgress(75);
//
//        // Attendance
//        enrollment.setAttendanceRate(85.0);
//        enrollment.setTotalClasses(10);
//        enrollment.setAttendedClasses(8);
//        enrollment.setAbsentClasses(2);
//        enrollment.setLateClasses(0);
//
//        enrollment.setProgramCode(chinese101.getProgramCode());
//        enrollment.setLevelNumber(chinese101.getLevelNumber());
//        enrollment.setEnrollmentMode(amanda.getEnrollmentMode());
//        enrollment.setHoursPerWeek(chinese101.getHoursPerWeek());
//        enrollment.setTuitionAmount(960.0);
//        enrollment.setTuitionPaid(true);
//        enrollment.setPaymentMethod("CREDIT_CARD");
//
//        Enrollment saved = enrollmentRepository.save(enrollment);
//
//        // Update course and student counts
//        chinese101.enrollStudent();
//        courseRepository.save(chinese101);
//
//        amanda.enrollInCourse(chinese101.getHoursPerWeek());
//        studentRepository.save(amanda);
//
//        log.info("Created enrollment: {} -> {}", saved.getStudentName(), saved.getCourseName());
//
//    }
//
//    private void createApplications() {
//        log.info("Creating applications...");
//
//        // Find next available session (UPCOMING or ACTIVE)
//        LocalDate today = LocalDate.now();
//        Session targetSession = sessionRepository.findAll().stream()
//                .filter(s -> s.getStartDate().isAfter(today) ||
//                        (s.getStatus() == SessionStatus.ACTIVE))
//                .min((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()))
//                .orElse(null);
//
//        if (targetSession == null) {
//            log.warn("No available session found for applications");
//            return;
//        }
//
//        log.info("Using session {} for applications", targetSession.getSessionCode());
//
//        // Application 1: SUBMITTED
//        Application app1 = new Application();
//        app1.setApplicationId("A202500001");
//        app1.setFirstName("Michael");
//        app1.setLastName("Johnson");
//        app1.setEmail("michael.j@email.com");
//        app1.setPhone("555-1001");
//        app1.setAddress("123 Oak Street, New York");
//        app1.setDateOfBirth(LocalDate.of(1995, 5, 15));
//        app1.setGender("MALE");
//        app1.setEmergencyContact("Sarah Johnson");
//        app1.setEmergencyPhone("555-1002");
//        app1.setEmailVerified(false);
//        app1.setPhoneVerified(false);
//        app1.setApplicationDate(LocalDate.now().minusDays(2));
//        app1.setStudentType(StudentType.TOURIST);
//        app1.setRegistrationType(RegistrationType.ONLINE);
//        app1.setTargetSessionCode(targetSession.getSessionCode());
//        app1.setTargetProgram("ESL");
//        app1.setEduLevel("BACHELOR");
//        app1.setCurrentOccupation("EMPLOYED");
//        app1.setLanguageBG("BEGINNER");
//        app1.setNativeLanguage("ENGLISH");
//        app1.setTargetLanguage("CHINESE");
//        app1.setLanguageTestScore("");
//        app1.setApplicationStatus(ApplicationStatus.SUBMITTED);
//        applicationRepository.save(app1);
//
//        // Application 2: UNDER_REVIEW
//        Application app2 = new Application();
//        app2.setApplicationId("A202500002");
//        app2.setFirstName("Sophie");
//        app2.setLastName("Martin");
//        app2.setEmail("sophie.m@email.com");
//        app2.setPhone("555-2001");
//        app2.setAddress("456 Elm Avenue, Boston");
//        app2.setDateOfBirth(LocalDate.of(1998, 8, 22));
//        app2.setGender("FEMALE");
//        app2.setEmergencyContact("Pierre Martin");
//        app2.setEmergencyPhone("555-2002");
//        app2.setEmailVerified(false);
//        app2.setPhoneVerified(false);
//        app2.setApplicationDate(LocalDate.now().minusDays(5));
//        app2.setStudentType(StudentType.TOURIST);
//        app2.setRegistrationType(RegistrationType.ONLINE);
//        app2.setTargetSessionCode(targetSession.getSessionCode());
//        app2.setTargetProgram("ESL");
//        app2.setEduLevel("MASTER");
//        app2.setCurrentOccupation("STUDENT");
//        app2.setLanguageBG("INTERMEDIATE");
//        app2.setNativeLanguage("FRENCH");
//        app2.setTargetLanguage("ENGLISH");
//        app2.setLanguageTestScore("IELTS 6.5");
//        app2.setApplicationStatus(ApplicationStatus.UNDER_REVIEW);
//        app2.setReviewedBy("E202500001");
//        app2.setReviewedDate(LocalDate.now().minusDays(1));
//        applicationRepository.save(app2);
//
//        // Application 3: APPROVED
//        Application app3 = new Application();
//        app3.setApplicationId("A202500003");
//        app3.setFirstName("Kenji");
//        app3.setLastName("Tanaka");
//        app3.setEmail("kenji.t@email.com");
//        app3.setPhone("555-3001");
//        app3.setAddress("789 Pine Road, San Francisco");
//        app3.setDateOfBirth(LocalDate.of(2000, 3, 10));
//        app3.setGender("MALE");
//        app3.setEmergencyContact("Yuki Tanaka");
//        app3.setEmergencyPhone("555-3002");
//        app3.setEmailVerified(true);
//        app3.setPhoneVerified(false);
//        app3.setApplicationDate(LocalDate.now().minusDays(10));
//        app3.setStudentType(StudentType.TOURIST);
//        app3.setRegistrationType(RegistrationType.ONLINE);
//        app3.setTargetSessionCode(targetSession.getSessionCode());
//        app2.setTargetProgram("ESL");
//        app3.setEduLevel("BACHELOR");
//        app3.setCurrentOccupation("EMPLOYED");
//        app3.setLanguageBG("ELEMENTARY");
//        app3.setNativeLanguage("JAPANESE");
//        app3.setTargetLanguage("ENGLISH");
//        app3.setLanguageTestScore("TOEFL 75");
//        app3.setApplicationStatus(ApplicationStatus.APPROVED);
//        app3.setReviewNotes("Good language background, approved for enrollment");
//        app3.setReviewedBy("E202500001");
//        app3.setReviewedDate(LocalDate.now().minusDays(1));
//        applicationRepository.save(app3);
//
//        // Application 4: REJECTED
//        Application app4 = new Application();
//        app4.setApplicationId("A202500004");
//        app4.setFirstName("Alex");
//        app4.setLastName("Brown");
//        app4.setEmail("alex.b@email.com");
//        app4.setPhone("555-4001");
//        app4.setAddress("321 Maple Drive, Chicago");
//        app4.setDateOfBirth(LocalDate.of(2002, 11, 5));
//        app4.setGender("OTHER");
//        app4.setEmergencyContact("Linda Brown");
//        app4.setEmergencyPhone("555-4002");
//        app4.setEmailVerified(false);
//        app4.setPhoneVerified(false);
//        app4.setApplicationDate(LocalDate.now().minusDays(15));
//        app4.setStudentType(StudentType.TOURIST);
//        app4.setRegistrationType(RegistrationType.ONLINE);
//        app4.setTargetSessionCode(targetSession.getSessionCode());
//        app4.setTargetProgram("EAP");
//        app4.setEduLevel("HIGH_SCHOOL");
//        app4.setCurrentOccupation("STUDENT");
//        app4.setLanguageBG("BEGINNER");
//        app4.setNativeLanguage("ENGLISH");
//        app4.setTargetLanguage("CHINESE");
//        app4.setLanguageTestScore("");
//        app4.setApplicationStatus(ApplicationStatus.REJECTED);
//        app4.setReviewNotes("Does not meet minimum requirements");
//        app4.setRejectionReason("Minimum B2 level required for TOEFL prep course");
//        app4.setReviewedBy("E202500001");
//        app4.setReviewedDate(LocalDate.now().minusDays(12));
//        applicationRepository.save(app4);
//
//        // Application 5: Minor with Guardian
//        Application app5 = new Application();
//        app5.setApplicationId("A202500005");
//        app5.setFirstName("Emma");
//        app5.setLastName("Wilson");
//        app5.setEmail("emma.w@email.com");
//        app5.setPhone("555-5001");
//        app5.setAddress("567 Birch Lane, Seattle");
//        app5.setDateOfBirth(LocalDate.of(2010, 7, 20));
//        app5.setGender("FEMALE");
//        app5.setEmergencyContact("Robert Wilson");
//        app5.setEmergencyPhone("555-5002");
//        app5.setGuardianName("Robert Wilson");
//        app5.setGuardianPhone("555-5002");
//        app5.setGuardianEmail("robert.w@email.com");
//        app5.setEmailVerified(false);
//        app5.setPhoneVerified(false);
//        app5.setApplicationDate(LocalDate.now().minusDays(1));
//        app5.setStudentType(StudentType.LOCAL);
//        app5.setRegistrationType(RegistrationType.ONLINE);
//        app5.setTargetSessionCode(targetSession.getSessionCode());
//        app5.setTargetProgram("EAP");
//        app5.setEduLevel("HIGH_SCHOOL");
//        app5.setCurrentOccupation("STUDENT");
//        app5.setLanguageBG("BEGINNER");
//        app5.setNativeLanguage("ENGLISH");
//        app5.setTargetLanguage("SPANISH");
//        app5.setLanguageTestScore("");
//        app5.setApplicationStatus(ApplicationStatus.SUBMITTED);
//        applicationRepository.save(app5);
//
//        log.info("Created 5 applications");
//    }

// BACKUP

//package io.rubyxzzz.lms.backend.config;
//
//import io.rubyxzzz.lms.backend.model.*;
//import io.rubyxzzz.lms.backend.repository.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//


///**
// * Initialize demo data for development/testing
// * Run once on application startup
// */



//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements ApplicationRunner {
//
//    private final SessionRepo sessionRepository;
//    private final StudentRepo studentRepository;
//    private final InstructorRepo instructorRepository;
//    private final AdminRepo adminRepository;
//    private final CourseRepo courseRepository;
//    private final EnrollmentRepo enrollmentRepository;
//    private final ApplicationRepo applicationRepository;
//

//    @Override
//    public void run(ApplicationArguments args) {
//        // Only initialize if database is empty
//        if (sessionRepository.count() > 0) {
//            log.info("Database already initialized, skipping data initialization");
//            return;
//        }
//
//        log.info("Starting data initialization...");
//
//        try {
//            // 1. Create Sessions (2025)
//            createSessions();
//
//            // 2. Create Admins
//            createAdmins();
//
//            // 3. Create Instructors
//            createInstructors();
//
//            // 4. Create Students
//            createStudents();
//
//            // 5. Create Courses
//            createCourses();
//
//            // 6. Create Enrollments
//            createEnrollments();
//
//            // 7. Create Applications
//            createApplications();
//
//            log.info("Data initialization completed successfully!");
//        } catch (Exception e) {
//            log.error("Error during data initialization", e);
//        }
//    }
//

//    private void createSessions() {
//        log.info("Creating sessions...");
//
//        Session[] sessions = {
//                createSession(1, 2025, LocalDate.of(2025, 1, 6), LocalDate.of(2025, 2, 28)),
//                createSession(2, 2025, LocalDate.of(2025, 3, 3), LocalDate.of(2025, 4, 25)),
//                createSession(3, 2025, LocalDate.of(2025, 4, 28), LocalDate.of(2025, 6, 20)),
//                createSession(4, 2025, LocalDate.of(2025, 6, 23), LocalDate.of(2025, 8, 15)),
//                createSession(5, 2025, LocalDate.of(2025, 8, 18), LocalDate.of(2025, 10, 10)),
//                createSession(6, 2025, LocalDate.of(2025, 10, 13), LocalDate.of(2025, 12, 5)),
//
//                createSession(1, 2026, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 27)),
//                createSession(2, 2026, LocalDate.of(2026, 3, 2), LocalDate.of(2026, 4, 24))
//        };
//
//        for (Session session : sessions) {
//            sessionRepository.save(session);
//        }
//
//        log.info("Created {} sessions", sessions.length);
//    }
//
//    private Session createSession(int sessionNumber, int year, LocalDate startDate, LocalDate endDate) {
//        Session session = new Session();
//        session.setSessionNumber(sessionNumber);
//        session.setYear(year);
//        session.setStartDate(startDate);
//        session.setEndDate(endDate);
//        session.setWeeks(8);
//
//        // Set registration dates
//        session.setRegistrationOpenDate(startDate.minusWeeks(4));
//        session.setRegistrationDeadline(startDate.minusWeeks(1));
//        session.setPaymentDeadline(startDate.plusDays(3));
//        session.setAddDropDeadline(startDate.plusWeeks(2));
//        session.setWithdrawDeadline(startDate.plusWeeks(6));
//
//        // Set status based on current date
//        LocalDate now = LocalDate.now();
//        if (now.isBefore(startDate)) {
//            session.setStatus(SessionStatus.UPCOMING);
//        } else if (now.isAfter(endDate)) {
//            session.setStatus(SessionStatus.COMPLETED);
//        } else {
//            session.setStatus(SessionStatus.ACTIVE);
//        }
//
//        return session;
//    }
//
//    private void createAdmins() {
//        log.info("Creating admins...");
//
//        // Super Admin
//        Admin superAdmin = new Admin();
//        superAdmin.setFirstName("Ruby");
//        superAdmin.setLastName("Zhang");
//        superAdmin.setEmail("admin@school.com");
//        superAdmin.setPassword("admin123"); // TODO: Hash password
//        superAdmin.setPhone("555-0001");
//        superAdmin.setEmployeeId("E202500001");
//        superAdmin.setDepartment("Administration");
//        superAdmin.setPosition("Super Administrator");
//        superAdmin.setIsSuperAdmin(true);
//        superAdmin.setStatus(UserStatus.ACTIVE);
//        adminRepository.save(superAdmin);
//
//        log.info("Created 1 admin");
//    }
//
//    private void createInstructors() {
//        log.info("Creating instructors...");
//
//        Instructor[] instructors = {
//                createInstructor("Yang", "Li", "li@school.com", "E202500101", "Language"),
//                createInstructor("John", "Smith", "smith@school.com", "E202500102", "Language"),
//                createInstructor("Eric", "Garcia", "garcia@school.com", "E202500103", "Language"),
//                createInstructor("Amber", "Dubois", "dubois@school.com", "E202500104", "Language")
//        };
//
//        for (Instructor instructor : instructors) {
//            instructorRepository.save(instructor);
//        }
//
//        log.info("Created {} instructors", instructors.length);
//    }
//
//    private Instructor createInstructor(String firstName, String lastName, String email,
//                                        String employeeId, String department) {
//        Instructor instructor = new Instructor();
//        instructor.setFirstName(firstName);
//        instructor.setLastName(lastName);
//        instructor.setEmail(email);
//        instructor.setPassword("instructor123");
//        instructor.setPhone("555-010" + employeeId.substring(employeeId.length() - 1));
//        instructor.setEmployeeId(employeeId);
//        instructor.setDepartment(department);
//        instructor.setOfficeHours("Mon-Fri 2:00-4:00 PM");
//        instructor.setStatus(UserStatus.ACTIVE);
//        return instructor;
//    }
//
//    private void createStudents() {
//        log.info("Creating students...");
//
//        Student[] students = {
//                createStudent("Amanda", "Chen", "amanda@school.com", "S202500001", CEFRLevel.B1),
//                createStudent("John", "Smith", "john@school.com", "S202500002", CEFRLevel.A2),
//                createStudent("Maria", "Rodriguez", "maria@school.com", "S202500003", CEFRLevel.B2),
//                createStudent("Wei", "Zhang", "wei@school.com", "S202500004", CEFRLevel.A1)
//        };
//
//        for (Student student : students) {
//            studentRepository.save(student);
//        }
//
//        log.info("Created {} students", students.length);
//    }
//
//    private Student createStudent(String firstName, String lastName, String email,
//                                  String studentId, CEFRLevel currentLevel) {
//        Student student = new Student();
//        student.setFirstName(firstName);
//        student.setLastName(lastName);
//        student.setEmail(email);
//        student.setPassword("student123"); // TODO: Hash password
//        student.setPhone("555-020" + studentId.substring(studentId.length() - 1));
//        student.setStudentId(studentId);
//        student.setStudentType(StudentType.LOCAL);
//        student.setRegistrationType(RegistrationType.WALK_IN);
//        student.setCurProgramCode("ESL");
//        student.setCurLevelNumber(3);
//        student.setCurCEFRLevel(currentLevel);
//        student.setNativeLanguage("English");
//        student.setTargetLanguage("Chinese");
//        student.setStatus(UserStatus.ACTIVE);
//        student.setDateOfBirth(LocalDate.of(2000, 1, 1));
//
//        return student;
//    }
//
//    private void createCourses() {
//        log.info("Creating courses...");
//
//        // Get current session (should be 2025-S5 based on October date)
//        Session currentSession = sessionRepository.findByYearAndNumber(2025, 5)
//                .orElseThrow(() -> new RuntimeException("Session 2025-S5 not found"));
//
//        // Get instructors
//        Instructor li = instructorRepository.findByEmail("li@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Li not found"));
//        Instructor smith = instructorRepository.findByEmail("smith@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Smith not found"));
//        Instructor garcia = instructorRepository.findByEmail("garcia@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Garcia not found"));
//        Instructor dubois = instructorRepository.findByEmail("dubois@school.com")
//                .orElseThrow(() -> new RuntimeException("Instructor Dubois not found"));
//
//        Course[] courses = {
//                createCourse("GE-101", "Chinese 101", "CHINESE", CEFRLevel.A1,
//                        currentSession, li, "Mon, Wed 2:00-3:30 PM", "Room 201"),
//                createCourse("GE-102", "English Advanced", "ENGLISH", CEFRLevel.B2,
//                        currentSession, smith, "Tue, Thu 10:00-11:30 AM", "Room 305"),
//                createCourse("GE-103", "Spanish Basics", "SPANISH", CEFRLevel.A1,
//                        currentSession, garcia, "Mon, Wed, Fri 3:00-4:00 PM", "Room 302"),
//                createCourse("GE-104", "French Conversation", "FRENCH", CEFRLevel.B1,
//                        currentSession, dubois, "Thu 4:00-5:30 PM", "Online")
//        };
//
//        for (Course course : courses) {
//            courseRepository.save(course);
//        }
//
//        log.info("Created {} courses", courses.length);
//    }
//
//    private Course createCourse(String courseCode, String courseName, String language,
//                                CEFRLevel cefrLevel, Session session, Instructor instructor,
//                                String schedule, String location) {
//        Course course = new Course();
//        course.setCourseCode(courseCode);
//        course.setCourseName(courseName);
//        course.setCourseDescription("Introduction to " + language);
//        course.setProgramCode("ESL");
//        course.setLevelNumber(1);
//        course.setCourseLevelName("L1");
//        course.setMinCEFRLevel(null);
//        course.setTargetCEFRLevel(cefrLevel);
//        course.setIsRequired(true);
//        course.setHoursPerWeek(9);
//
//        course.setLanguage(language);
//
//        // Session info
//        course.setSessionNumber(session.getSessionNumber());
//        course.setYear(session.getYear());
//        course.setStartDate(session.getStartDate());
//        course.setEndDate(session.getEndDate());
//        course.setWeeks(8);
//
//        // Schedule
//        course.setSchedule(schedule);
//        course.setLocation(location);
//        course.setCourseFormat(location.equals("Online") ? CourseFormat.ONLINE : CourseFormat.IN_PERSON);
//
//        // Instructor
//        course.setInstructorUUID(instructor.getId());
//        course.setInstructorId(instructor.getEmployeeId());
//        course.setInstructorName(instructor.getFullName());
//        course.setInstructorEmail(instructor.getEmail());
//
//        // Capacity
//        course.setCapacity(30);
//        course.setMinEnrollment(5);
//
//        // Pricing
//        course.setTuition(960.0);
//        course.setCurrency("USD");
//
//        // Requirements
//        course.setPassingGrade(80.0);
//        course.setAttendanceRequirement(80.0);
//        course.setAssignmentRequirement(80.0);
//
//        // Status
//        course.setStatus(CourseStatus.ENROLL_OPEN);
//
//        return course;
//    }
//
//    private void createEnrollments() {
//        log.info("Creating enrollments...");
//
//        // Get student Amanda Chen
//        Student amanda = studentRepository.findByStudentId("S202500001")
//                .orElseThrow(() -> new RuntimeException("Student Amanda not found"));
//
//        // Get course Chinese 101
//        Course chinese101 = courseRepository.findByCourseCode("GE-101")
//                .orElseThrow(() -> new RuntimeException("Course Chinese 101 not found"));
//
//        // Create enrollment
//        Enrollment enrollment = new Enrollment();
//
//        // UUID relations
//        enrollment.setStudentUUID(amanda.getId());
//        enrollment.setCourseUUID(chinese101.getId());
//
//        // Display IDs
//        enrollment.setStudentId(amanda.getStudentId());
//        enrollment.setStudentName(amanda.getFullName());
//        enrollment.setCourseCode(chinese101.getCourseCode());
//        enrollment.setCourseName(chinese101.getCourseName());
//
//        // Session
//        enrollment.setSessionNumber(chinese101.getSessionNumber());
//        enrollment.setYear(chinese101.getYear());
//        enrollment.setSessionCode(chinese101.getYear() + "-S" + chinese101.getSessionNumber());
//
//        // Timeline
//        enrollment.setEnrolledTime(LocalDateTime.now());
//        enrollment.setStatus(EnrollmentStatus.ENROLLED);
//        enrollment.setProgress(75);
//
//        // Attendance
//        enrollment.setAttendanceRate(85.0);
//        enrollment.setTotalClasses(10);
//        enrollment.setAttendedClasses(8);
//        enrollment.setAbsentClasses(2);
//        enrollment.setLateClasses(0);
//
//        enrollment.setProgramCode(chinese101.getProgramCode());
//        enrollment.setLevelNumber(chinese101.getLevelNumber());
//        enrollment.setEnrollmentMode(amanda.getEnrollmentMode());
//        enrollment.setHoursPerWeek(chinese101.getHoursPerWeek());
//        enrollment.setTuitionAmount(960.0);
//        enrollment.setTuitionPaid(true);
//        enrollment.setPaymentMethod("CREDIT_CARD");
//
//        Enrollment saved = enrollmentRepository.save(enrollment);
//
//        // Update course and student counts
//        chinese101.enrollStudent();
//        courseRepository.save(chinese101);
//
//        amanda.enrollInCourse(chinese101.getHoursPerWeek());
//        studentRepository.save(amanda);
//
//        log.info("Created enrollment: {} -> {}", saved.getStudentName(), saved.getCourseName());
//
//    }
//
//    private void createApplications() {
//        log.info("Creating applications...");
//
//        // Find next available session (UPCOMING or ACTIVE)
//        LocalDate today = LocalDate.now();
//        Session targetSession = sessionRepository.findAll().stream()
//                .filter(s -> s.getStartDate().isAfter(today) ||
//                        (s.getStatus() == SessionStatus.ACTIVE))
//                .min((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()))
//                .orElse(null);
//
//        if (targetSession == null) {
//            log.warn("No available session found for applications");
//            return;
//        }
//
//        log.info("Using session {} for applications", targetSession.getSessionCode());
//
//        // Application 1: SUBMITTED
//        Application app1 = new Application();
//        app1.setApplicationId("A202500001");
//        app1.setFirstName("Michael");
//        app1.setLastName("Johnson");
//        app1.setEmail("michael.j@email.com");
//        app1.setPhone("555-1001");
//        app1.setAddress("123 Oak Street, New York");
//        app1.setDateOfBirth(LocalDate.of(1995, 5, 15));
//        app1.setGender("MALE");
//        app1.setEmergencyContact("Sarah Johnson");
//        app1.setEmergencyPhone("555-1002");
//        app1.setEmailVerified(false);
//        app1.setPhoneVerified(false);
//        app1.setApplicationDate(LocalDate.now().minusDays(2));
//        app1.setStudentType(StudentType.TOURIST);
//        app1.setRegistrationType(RegistrationType.ONLINE);
//        app1.setTargetSessionCode(targetSession.getSessionCode());
//        app1.setTargetProgram("ESL");
//        app1.setEduLevel("BACHELOR");
//        app1.setCurrentOccupation("EMPLOYED");
//        app1.setLanguageBG("BEGINNER");
//        app1.setNativeLanguage("ENGLISH");
//        app1.setTargetLanguage("CHINESE");
//        app1.setLanguageTestScore("");
//        app1.setApplicationStatus(ApplicationStatus.SUBMITTED);
//        applicationRepository.save(app1);
//
//        // Application 2: UNDER_REVIEW
//        Application app2 = new Application();
//        app2.setApplicationId("A202500002");
//        app2.setFirstName("Sophie");
//        app2.setLastName("Martin");
//        app2.setEmail("sophie.m@email.com");
//        app2.setPhone("555-2001");
//        app2.setAddress("456 Elm Avenue, Boston");
//        app2.setDateOfBirth(LocalDate.of(1998, 8, 22));
//        app2.setGender("FEMALE");
//        app2.setEmergencyContact("Pierre Martin");
//        app2.setEmergencyPhone("555-2002");
//        app2.setEmailVerified(false);
//        app2.setPhoneVerified(false);
//        app2.setApplicationDate(LocalDate.now().minusDays(5));
//        app2.setStudentType(StudentType.TOURIST);
//        app2.setRegistrationType(RegistrationType.ONLINE);
//        app2.setTargetSessionCode(targetSession.getSessionCode());
//        app2.setTargetProgram("ESL");
//        app2.setEduLevel("MASTER");
//        app2.setCurrentOccupation("STUDENT");
//        app2.setLanguageBG("INTERMEDIATE");
//        app2.setNativeLanguage("FRENCH");
//        app2.setTargetLanguage("ENGLISH");
//        app2.setLanguageTestScore("IELTS 6.5");
//        app2.setApplicationStatus(ApplicationStatus.UNDER_REVIEW);
//        app2.setReviewedBy("E202500001");
//        app2.setReviewedDate(LocalDate.now().minusDays(1));
//        applicationRepository.save(app2);
//
//        // Application 3: APPROVED
//        Application app3 = new Application();
//        app3.setApplicationId("A202500003");
//        app3.setFirstName("Kenji");
//        app3.setLastName("Tanaka");
//        app3.setEmail("kenji.t@email.com");
//        app3.setPhone("555-3001");
//        app3.setAddress("789 Pine Road, San Francisco");
//        app3.setDateOfBirth(LocalDate.of(2000, 3, 10));
//        app3.setGender("MALE");
//        app3.setEmergencyContact("Yuki Tanaka");
//        app3.setEmergencyPhone("555-3002");
//        app3.setEmailVerified(true);
//        app3.setPhoneVerified(false);
//        app3.setApplicationDate(LocalDate.now().minusDays(10));
//        app3.setStudentType(StudentType.TOURIST);
//        app3.setRegistrationType(RegistrationType.ONLINE);
//        app3.setTargetSessionCode(targetSession.getSessionCode());
//        app2.setTargetProgram("ESL");
//        app3.setEduLevel("BACHELOR");
//        app3.setCurrentOccupation("EMPLOYED");
//        app3.setLanguageBG("ELEMENTARY");
//        app3.setNativeLanguage("JAPANESE");
//        app3.setTargetLanguage("ENGLISH");
//        app3.setLanguageTestScore("TOEFL 75");
//        app3.setApplicationStatus(ApplicationStatus.APPROVED);
//        app3.setReviewNotes("Good language background, approved for enrollment");
//        app3.setReviewedBy("E202500001");
//        app3.setReviewedDate(LocalDate.now().minusDays(1));
//        applicationRepository.save(app3);
//
//        // Application 4: REJECTED
//        Application app4 = new Application();
//        app4.setApplicationId("A202500004");
//        app4.setFirstName("Alex");
//        app4.setLastName("Brown");
//        app4.setEmail("alex.b@email.com");
//        app4.setPhone("555-4001");
//        app4.setAddress("321 Maple Drive, Chicago");
//        app4.setDateOfBirth(LocalDate.of(2002, 11, 5));
//        app4.setGender("OTHER");
//        app4.setEmergencyContact("Linda Brown");
//        app4.setEmergencyPhone("555-4002");
//        app4.setEmailVerified(false);
//        app4.setPhoneVerified(false);
//        app4.setApplicationDate(LocalDate.now().minusDays(15));
//        app4.setStudentType(StudentType.TOURIST);
//        app4.setRegistrationType(RegistrationType.ONLINE);
//        app4.setTargetSessionCode(targetSession.getSessionCode());
//        app4.setTargetProgram("EAP");
//        app4.setEduLevel("HIGH_SCHOOL");
//        app4.setCurrentOccupation("STUDENT");
//        app4.setLanguageBG("BEGINNER");
//        app4.setNativeLanguage("ENGLISH");
//        app4.setTargetLanguage("CHINESE");
//        app4.setLanguageTestScore("");
//        app4.setApplicationStatus(ApplicationStatus.REJECTED);
//        app4.setReviewNotes("Does not meet minimum requirements");
//        app4.setRejectionReason("Minimum B2 level required for TOEFL prep course");
//        app4.setReviewedBy("E202500001");
//        app4.setReviewedDate(LocalDate.now().minusDays(12));
//        applicationRepository.save(app4);
//
//        // Application 5: Minor with Guardian
//        Application app5 = new Application();
//        app5.setApplicationId("A202500005");
//        app5.setFirstName("Emma");
//        app5.setLastName("Wilson");
//        app5.setEmail("emma.w@email.com");
//        app5.setPhone("555-5001");
//        app5.setAddress("567 Birch Lane, Seattle");
//        app5.setDateOfBirth(LocalDate.of(2010, 7, 20));
//        app5.setGender("FEMALE");
//        app5.setEmergencyContact("Robert Wilson");
//        app5.setEmergencyPhone("555-5002");
//        app5.setGuardianName("Robert Wilson");
//        app5.setGuardianPhone("555-5002");
//        app5.setGuardianEmail("robert.w@email.com");
//        app5.setEmailVerified(false);
//        app5.setPhoneVerified(false);
//        app5.setApplicationDate(LocalDate.now().minusDays(1));
//        app5.setStudentType(StudentType.LOCAL);
//        app5.setRegistrationType(RegistrationType.ONLINE);
//        app5.setTargetSessionCode(targetSession.getSessionCode());
//        app5.setTargetProgram("EAP");
//        app5.setEduLevel("HIGH_SCHOOL");
//        app5.setCurrentOccupation("STUDENT");
//        app5.setLanguageBG("BEGINNER");
//        app5.setNativeLanguage("ENGLISH");
//        app5.setTargetLanguage("SPANISH");
//        app5.setLanguageTestScore("");
//        app5.setApplicationStatus(ApplicationStatus.SUBMITTED);
//        applicationRepository.save(app5);
//
//        log.info("Created 5 applications");
//    }



























































































































































































































































































































































































































// set

























































































































































































































































































































































































































// set