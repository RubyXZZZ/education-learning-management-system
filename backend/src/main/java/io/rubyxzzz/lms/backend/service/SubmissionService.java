package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.request.CreateSubmReq;
import io.rubyxzzz.lms.backend.dto.request.GradeSubmReq;
import io.rubyxzzz.lms.backend.dto.response.SubmissionRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.SubmissionMapper;
import io.rubyxzzz.lms.backend.model.*;
import io.rubyxzzz.lms.backend.repository.AssignmentRepo;
import io.rubyxzzz.lms.backend.repository.EnrollmentRepo;
import io.rubyxzzz.lms.backend.repository.SubmissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Responsibilities:
 * - Create submissions
 * - Resubmit (create new attempt)
 * - Grade submissions
 * - Handle missing submissions (auto)
 */
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepo submissionRepo;
    private final AssignmentRepo assignmentRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final SubmissionMapper submissionMapper;

    // create or resubmit submission
    @Transactional
    public SubmissionRes createSubmission(CreateSubmReq request, String currentStudentId) {
        // get assignment
        Assignment assignment = assignmentRepo.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assignment", request.getAssignmentId()
                ));

        if (!assignment.acceptsSubmissions()) {
            throw new IllegalStateException("Assignment does not accept submissions");
        }

        // check if has existing submission
        Optional<Submission> existing = submissionRepo.findLatestByAssignmentAndStudent(
                request.getAssignmentId(),
                currentStudentId
        );

        // If exists, validate and mark as not latest
        if (existing.isPresent()) {
            Submission old = existing.get();

            if (old.getAttemptNumber() >= assignment.getMaxAttempts()) {
                throw new IllegalStateException(
                        "Maximum attempts reached: " + assignment.getMaxAttempts()
                );
            }
            // Mark old as not latest
            old.setIsLatest(false);
            submissionRepo.save(old);
        }

        // Create new submission
        Submission submission = new Submission();
        BeanUtils.copyProperties(request, submission);

        // set relations
        Assignment assignmentRef = new Assignment();
        assignmentRef.setId(request.getAssignmentId());
        submission.setAssignment(assignmentRef);

        Student studentRef = new Student();
        studentRef.setId(currentStudentId);
        submission.setStudent(studentRef);

        // Set metadata
        submission.setAttemptNumber(existing.map(s -> s.getAttemptNumber() + 1).orElse(1));
        submission.setIsLatest(true);
        submission.setSubmittedAt(LocalDateTime.now());

        // Check if late
        LocalDateTime dueDate = assignment.getDueDate();
        if (dueDate != null && LocalDateTime.now().isAfter(dueDate)) {
            submission.setStatus(SubmissionStatus.LATE);
        } else {
            submission.setStatus(SubmissionStatus.SUBMITTED);
        }

        return submissionMapper.toResponse(submissionRepo.save(submission));
    }

    // grade submission
    @Transactional
    public SubmissionRes gradeSubmission(GradeSubmReq request, String gradedBy) {
        // Find or create submission
        Submission submission = submissionRepo
                .findLatestByAssignmentAndStudent(
                        request.getAssignmentId(),
                        request.getStudentId()
                )
                .orElseGet(() -> createMissingSubmission(
                        request.getAssignmentId(),
                        request.getStudentId()
                ));

        // Update grade
        submission.setGrade(request.getGrade());
        submission.setFeedback(request.getFeedback());
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedBy(gradedBy);
        submission.setStatus(SubmissionStatus.GRADED);

        return submissionMapper.toResponse(submissionRepo.save(submission));
    }

    // create a missing submission record (for grading convenience)
    private Submission createMissingSubmission(String assignmentId, String studentId) {
        Submission submission = new Submission();

        Assignment assignment = new Assignment();
        assignment.setId(assignmentId);
        submission.setAssignment(assignment);

        Student student = new Student();
        student.setId(studentId);
        submission.setStudent(student);

        submission.setAttemptNumber(0);
        submission.setIsLatest(true);
        submission.setStatus(SubmissionStatus.MISSING);

        return submission;
    }


    // get submission by id
    @Transactional(readOnly = true)
    public SubmissionRes getSubmission(String submissionId) {
        Submission submission = submissionRepo.findByIdWithRelations(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", submissionId));
        return submissionMapper.toResponse(submission);
    }

    // get all submissions for assignment (including missing)
    @Transactional(readOnly = true)
    public List<SubmissionRes> getSubmissionsWithMissing(String assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", assignmentId));

        // Get submitted
        List<Submission> submissions = submissionRepo.findLatestByAssignment(assignmentId);
        List<SubmissionRes> results = submissionMapper.toResponseList(submissions);

        // If not past due, return submitted only
        if (LocalDateTime.now().isBefore(assignment.getDueDate())) {
            return results;
        }

        // Add MISSING for students who didn't submit
        Set<String> submittedIds = submissions.stream()
                .map(s -> s.getStudent().getId())
                .collect(Collectors.toSet());

        enrollmentRepo.findActiveBySectionId(assignment.getCourseSection().getId())
                .stream()
                .filter(e -> !submittedIds.contains(e.getStudent().getId()))
                .forEach(e -> results.add(buildMissingResponse(e.getStudent(), assignment)));

        return results;
    }
    // build missing submission response
    private SubmissionRes buildMissingResponse(Student student, Assignment assignment) {
        return SubmissionRes.builder()
                .assignmentId(assignment.getId())
                .assignmentTitle(assignment.getTitle())
                .totalPoints(assignment.getTotalPoints())
                .dueDate(assignment.getDueDate())
                .studentId(student.getId())
                .studentNumber(student.getStudentNumber())
                .studentName(student.getFullName())
                .studentEmail(student.getEmail())
                .status(SubmissionStatus.MISSING)
                .grade(0.0)
                .attemptNumber(0)
                .isLatest(true)
                .submittedAt(null)
                .build();
    }


    // get ungraded submissions by assignment
    @Transactional(readOnly = true)
    public List<SubmissionRes> getUngradedSubmissions(String assignmentId) {
        return submissionMapper.toResponseList(
                submissionRepo.findUngradedByAssignment(assignmentId)
        );
    }

    // get graded submissions
    @Transactional(readOnly = true)
    public List<SubmissionRes> getGradedSubmissions(String assignmentId) {
        return submissionMapper.toResponseList(
                submissionRepo.findGradedByAssignment(assignmentId)
        );
    }

    // get submissions by student (for instructor)
    @Transactional(readOnly = true)
    public List<SubmissionRes> getSubmissionsByStudent(String studentId) {
        return submissionMapper.toResponseList(
                submissionRepo.findLatestByStudent(studentId)
        );
    }


    // get student's latest submission for assignment(for instructor)
    @Transactional(readOnly = true)
    public SubmissionRes getStudentLatestSubmission(String assignmentId, String studentId) {
        Submission submission = submissionRepo.findLatestByAssignmentAndStudent(
                        assignmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No submission found for this assignment and student"
                ));
        return submissionMapper.toResponse(submission);
    }

    // get submission history(all attempts)
    @Transactional(readOnly = true)
    public List<SubmissionRes> getSubmissionHistory(String assignmentId, String studentId) {
        List<Submission> attempts = submissionRepo.findAllAttemptsByAssignmentAndStudent(
                assignmentId, studentId
        );
        return submissionMapper.toResponseList(attempts);
    }

    // delete submission
//    @Transactional
//    public void deleteSubmission(String submissionId) {
//        Submission submission = submissionRepo.findById(submissionId)
//                .orElseThrow(() -> new ResourceNotFoundException("Submission", submissionId));
//
//        if (submission.getStatus() == SubmissionStatus.GRADED) {
//            throw new IllegalStateException("Cannot delete graded submissions");
//        }
//
//        submissionRepo.delete(submission);
//    }

}
