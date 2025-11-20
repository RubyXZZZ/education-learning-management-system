package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.request.CreateAsgnReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateAsgnReq;
import io.rubyxzzz.lms.backend.dto.response.AssignmentRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.AssignmentMapper;
import io.rubyxzzz.lms.backend.model.Assignment;
import io.rubyxzzz.lms.backend.model.AssignmentType;
import io.rubyxzzz.lms.backend.model.CourseSection;
import io.rubyxzzz.lms.backend.model.Module;
import io.rubyxzzz.lms.backend.repository.AssignmentRepo;
import io.rubyxzzz.lms.backend.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepo assignmentRepo;
    private final AssignmentMapper assignmentMapper;

    // create assignment
    @Transactional
    public AssignmentRes createAssignment(CreateAsgnReq request) {
        Assignment assignment = new Assignment();
        BeanUtils.copyProperties(request, assignment);

        // set section relationships
        CourseSection section = new CourseSection();
        section.setId(request.getCourseSectionId());
        assignment.setCourseSection(section);

        // set module relationship if provided
//        if (request.getModuleId() != null) {
//            Module module = new Module();
//            module.setId(request.getModuleId());
//            assignment.setModule(module);
//        }

        // save
        Assignment savedAssignment = assignmentRepo.save(assignment);
        return assignmentMapper.toResponse(savedAssignment);

    }

    // update assignment
    @Transactional
    public AssignmentRes updateAssignment(String assignmentId, UpdateAsgnReq request) {
        Assignment assignment = assignmentRepo.findByIdWithRelations(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", assignmentId));

        // update properties
        UpdateUtil.copyNonNullProperties(request, assignment);

        // handle module change
//        if (request.getModuleId() != null) {
//            Module module = new Module();
//            module.setId(request.getModuleId());
//            assignment.setModule(module);
//        }

        Assignment updatedAssignment = assignmentRepo.save(assignment);
        return assignmentMapper.toResponse(updatedAssignment);
    }

    // get assignment by id
    public AssignmentRes getAssignment(String assignmentId) {
        Assignment assignment = assignmentRepo.findByIdWithRelations(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", assignmentId));
        return assignmentMapper.toResponse(assignment);
    }

    // get assignments by section(Instructor view)
    public List<AssignmentRes> getAssignmentsBySection(String sectionId) {
        return assignmentMapper.toResponseList(
                assignmentRepo.findByCourseSectionId(sectionId)
        );
    }

    // get published assignments by section (Student view)
    public List<AssignmentRes> getPublishedAssignmentsBySection(String sectionId) {
        return assignmentMapper.toResponseList(
                assignmentRepo.findPublishedBySection(sectionId)
        );
    }

    // get assignments by type
    public List<AssignmentRes> getAssignmentsBySectionAndType(String sectionId, AssignmentType type){
        return assignmentMapper.toResponseList(
                assignmentRepo.findBySectionAndType(sectionId, type)
        );
    }

    // get upcoming assignments (due soon within 7 days)
    public List<AssignmentRes> getUpcomingAssignments(String sectionId){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusDays(7);
        return assignmentMapper.toResponseList(
                assignmentRepo.findUpcomingAssignments(sectionId, now, until)
        );
    }

    // get overdue assignments
    public List<AssignmentRes> getOverdueAssignments(String sectionId) {
        return assignmentMapper.toResponseList(
                assignmentRepo.findOverdueAssignments(sectionId, LocalDateTime.now())
        );
    }

    // publish assignment
    @Transactional
    public AssignmentRes publishAssignment(String assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", assignmentId));

        assignment.setIsPublished(true);
        Assignment updatedAssignment = assignmentRepo.save(assignment);
        return assignmentMapper.toResponse(updatedAssignment);
    }

    // unpublish assignment
    @Transactional
    public AssignmentRes unpublishAssignment(String assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", assignmentId));

        assignment.setIsPublished(false);
        Assignment updatedAssignment = assignmentRepo.save(assignment);
        return assignmentMapper.toResponse(updatedAssignment);
    }

    // delete assignment
    @Transactional
    public void deleteAssignment(String assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", assignmentId));

        // MVP: delete directly without validation
        //  can validate no submissions exist
        assignmentRepo.delete(assignment);
    }
}

