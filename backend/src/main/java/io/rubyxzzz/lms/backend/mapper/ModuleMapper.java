package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.response.ModuleRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import io.rubyxzzz.lms.backend.model.Module;
@Component
@RequiredArgsConstructor
public class ModuleMapper {

    // convert Module entity to ModuleRes DTO
    public ModuleRes toResponse(Module module) {
        if (module == null) {
            return null;
        }

        ModuleRes response = new ModuleRes();
        BeanUtils.copyProperties(module, response);

        // add section ID
        if (module.getCourseSection() != null) {
            response.setCourseSectionId(module.getCourseSection().getId());
        }
        return response;
    }

    public List<ModuleRes> toResponseList(List<Module> modules) {
        if (modules == null) {
            return List.of();
        }
        return modules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

    }
}
