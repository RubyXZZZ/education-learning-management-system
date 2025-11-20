package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.request.CreateModuleReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateModuleReq;
import io.rubyxzzz.lms.backend.dto.response.ModuleRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.ModuleMapper;
import io.rubyxzzz.lms.backend.model.CourseSection;
import io.rubyxzzz.lms.backend.model.Module;
import io.rubyxzzz.lms.backend.repository.ModuleRepo;
import io.rubyxzzz.lms.backend.repository.SectionRepo;
import io.rubyxzzz.lms.backend.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Module Service
 * Business logic for course content modules
 */
@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepo moduleRepo;
    private final ModuleMapper moduleMapper;
    private final SectionRepo sectionRepo;

    // create a new module
    @Transactional
    public ModuleRes createModule(CreateModuleReq request) {
        // Build module entity
        Module module = new Module();
        BeanUtils.copyProperties(request, module);

        // set section reference
        CourseSection section = new CourseSection();
        section.setId(request.getCourseSectionId());
        module.setCourseSection(section);

        if (module.getOrderNum() == null) {
            Integer maxOrder = moduleRepo.getMaxOrderNumBySection(request.getCourseSectionId());
            module.setOrderNum(maxOrder + 1);
        }

        // Set default for isPublished if null
        if (module.getIsPublished() == null) {
            module.setIsPublished(false);
        }

        Module savedModule = moduleRepo.save(module);
        return moduleMapper.toResponse(savedModule);
    }

    // update a module
    @Transactional
    public ModuleRes updateModule(String moduleId, UpdateModuleReq request) {
        Module module = moduleRepo.findByIdWithSection(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));

        // Update properties
        UpdateUtil.copyNonNullProperties(request, module);

        //TODO: HANDLE reorder problem


        Module updatedModule = moduleRepo.save(module);
        return moduleMapper.toResponse(updatedModule);
    }

    // get module by Id
    public ModuleRes getModule(String moduleId) {
        Module module = moduleRepo.findByIdWithSection(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));
        return moduleMapper.toResponse(module);
    }

    // get modules by section( for instructor view )
    public List<ModuleRes> getModulesBySection(String sectionId) {
        return moduleMapper.toResponseList(
                moduleRepo.findAllBySection(sectionId)
        );
    }

    // get published modules by section( for student view)
    public List<ModuleRes> getPublishedModulesBySection(String sectionId) {
        return moduleMapper.toResponseList(
                moduleRepo.findPublishedBySection(sectionId)
        );
    }

    // publish module
    @Transactional
    public ModuleRes publishModule(String moduleId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));

        module.setIsPublished(true);
        return moduleMapper.toResponse(moduleRepo.save(module));
    }

    // unpublish module
    @Transactional
    public ModuleRes unpublishModule(String moduleId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));

        module.setIsPublished(false);
        return moduleMapper.toResponse(moduleRepo.save(module));
    }

    // delete module
    @Transactional
    public void deleteModule(String moduleId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));
        // MVP - delete module directly without validation
        moduleRepo.delete(module);
    }

    // reorder modules
    @Transactional
    public ModuleRes reorderModule(String moduleId, Integer newOrderNum) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));

        module.setOrderNum(newOrderNum);
        Module updatedModule = moduleRepo.save(module);
        return moduleMapper.toResponse(updatedModule);
    }
}
