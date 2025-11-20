package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.listItem.InstructorList;
import io.rubyxzzz.lms.backend.dto.response.InstructorRes;
import io.rubyxzzz.lms.backend.model.Instructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InstructorMapper {

    private final SectionMapper sectionMapper;

    /**
     * Convert Instructor to response (no sections)
     */
    public InstructorRes toResponse(Instructor instructor) {
        if (instructor == null) return null;

        InstructorRes response = new InstructorRes();
        BeanUtils.copyProperties(instructor, response);

        response.setFullName(instructor.getFullName());
        response.setAge(instructor.getAge());

        return response;
    }

    /**
     * Convert Instructor to response with sections
     * Use when instructor.getSections() is already loaded
     */
    public InstructorRes toResponseWithSections(Instructor instructor) {
        if (instructor == null) return null;

        InstructorRes response = new InstructorRes();
        BeanUtils.copyProperties(instructor, response);

        response.setFullName(instructor.getFullName());
        response.setAge(instructor.getAge());

        // Map sections
        if (instructor.getSections() != null) {
            response.setSections(
                    sectionMapper.toResponseList(instructor.getSections())
            );
        }

        return response;
    }

    public InstructorList toListItem(Instructor instructor) {
        if (instructor == null) return null;

        InstructorList item = new InstructorList();
        BeanUtils.copyProperties(instructor, item);
        item.setFullName(instructor.getFullName());

        return item;
    }

    public List<InstructorRes> toResponseList(List<Instructor> instructors) {
        if (instructors == null) return List.of();
        return instructors.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<InstructorList> toListItems(List<Instructor> instructors) {
        if (instructors == null) return List.of();
        return instructors.stream().map(this::toListItem).collect(Collectors.toList());
    }
}