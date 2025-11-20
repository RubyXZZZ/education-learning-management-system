package io.rubyxzzz.lms.backend.util;


import io.rubyxzzz.lms.backend.repository.StudentRepo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID Generator for business identifiers
 *
 * Generates human-readable IDs:
 * - Student ID: S2025001, S2025002, ...
 * - Employee ID: E2025001, E2025002, ...
 * - Course Code: GE-101, AE-202, ...
 *
 * Thread-safe using AtomicInteger
 */
@Slf4j
@Component
public class IdGenerator {

    private final StudentRepo studentRepo;
    // Atomic counters for thread safety
    private final AtomicInteger studentCounter = new AtomicInteger(0);
    private final AtomicInteger employeeCounter = new AtomicInteger(0);


    // Current year for ID generation
    private int currentYear = Year.now().getValue();

    public IdGenerator(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    @PostConstruct
    public void initialize() {
        int year = Year.now().getValue();
        String studentPrefix = "S" + year;
        String employeePrefix = "E" + year;

        // Initialize student counter
        Integer maxStudentSeq = studentRepo.findAll().stream()
                .map(s -> s.getStudentNumber())
                .filter(num -> num != null && num.startsWith(studentPrefix))
                .map(num -> Integer.parseInt(num.substring(5)))
                .max(Integer::compareTo)
                .orElse(0);

        studentCounter.set(maxStudentSeq);
//        employeeCounter.set(maxEmployeeSeq);

    }

    /**
     * Generate Student ID
     * Format: S{YEAR}{SEQUENCE}
     * Example: S202500001, S202500002
     */
    public String generateStudentNumber() {
        int sequence = studentCounter.incrementAndGet();
        return String.format("S%d%05d", currentYear, sequence);
    }

    /**
     * Generate Employee ID (for Instructor/Admin)
     * Format: E{YEAR}{SEQUENCE}
     * Example: E202500001, E202500002
     */
    public String generateEmployeeNumber() {
        int sequence = employeeCounter.incrementAndGet();
        return String.format("E%d%05d", currentYear, sequence);
    }




    /**
     * Reset counters (typically called at year change)
     * Should be called by scheduled job on Jan 1st
     */
    public void resetCounters() {
        this.currentYear = Year.now().getValue();
        this.studentCounter.set(0);
        this.employeeCounter.set(0);
    }
}