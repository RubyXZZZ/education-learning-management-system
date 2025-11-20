package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.request.CreateCoursePageReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateCoursePageReq;
import io.rubyxzzz.lms.backend.dto.response.CoursePageRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.CoursePageMapper;
import io.rubyxzzz.lms.backend.model.CoursePage;
import io.rubyxzzz.lms.backend.model.CourseSection;
import io.rubyxzzz.lms.backend.model.Module;
import io.rubyxzzz.lms.backend.repository.CoursePageRepo;
import io.rubyxzzz.lms.backend.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CoursePageService {

    private final CoursePageRepo coursePageRepo;
    private final CoursePageMapper coursePageMapper;

    //Create a new course page
    @Transactional
    public CoursePageRes createCoursePage(CreateCoursePageReq request) {
        // Build page entity
        CoursePage page = new CoursePage();
        BeanUtils.copyProperties(request, page);

        // Set section reference
        CourseSection section = new CourseSection();
        section.setId(request.getCourseSectionId());
        page.setCourseSection(section);

        // Set module reference
        Module module = new Module();
        module.setId(request.getModuleId());
        page.setModule(module);

        // Auto-generate orderNum within module
        if (page.getOrderNum() == null) {
            Integer maxOrder = coursePageRepo.getMaxOrderNumByModule(request.getModuleId());
            page.setOrderNum(maxOrder + 1);
        }

        // Set default for isPublished
        if (page.getIsPublished() == null) {
            page.setIsPublished(false);
        }

        CoursePage savedPage = coursePageRepo.save(page);
        return coursePageMapper.toResponse(savedPage);
    }

    //Update course page
    @Transactional
    public CoursePageRes updateCoursePage(String pageId, UpdateCoursePageReq request) {
        CoursePage page = coursePageRepo.findByIdWithRelations(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("CoursePage", pageId));

        // Handle module change
        if (request.getModuleId() != null &&
                !request.getModuleId().equals(page.getModule().getId())) {

            // Module changed - reset orderNum to end of new module
            Module newModule = new Module();
            newModule.setId(request.getModuleId());
            page.setModule(newModule);

            // Auto-generate new orderNum in new module
            Integer maxOrder = coursePageRepo.getMaxOrderNumByModule(request.getModuleId());
            page.setOrderNum(maxOrder + 1);
        } else {
            // Module not changed
            UpdateUtil.copyNonNullProperties(request, page);
        }

        // todo: HANDLE reorder problem


        CoursePage updatedPage = coursePageRepo.save(page);
        return coursePageMapper.toResponse(updatedPage);
    }

    //Get page by ID
    public CoursePageRes getCoursePage(String pageId) {
        CoursePage page = coursePageRepo.findByIdWithRelations(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("CoursePage", pageId));
        return coursePageMapper.toResponse(page);
    }

    //Get pages by module (instructor view - all pages)
    // MVP: LOAD all by section
    public List<CoursePageRes> getPagesBySection(String sectionId) {
        return coursePageMapper.toResponseList(
                coursePageRepo.findByCourseSectionId(sectionId)
        );
    }


    //Get published pages by section (student view)
    // MVP: LOAD all by section
    public List<CoursePageRes> getPublishedPagesBySection(String sectionId) {
        List<CoursePage> pages = coursePageRepo.findPublishedBySection(sectionId);

        // Filter by isVisibleToStudent()
        List<CoursePage> visiblePages = pages.stream()
                .filter(CoursePage::isVisibleToStudent)
                .collect(Collectors.toList());

        return coursePageMapper.toResponseList(visiblePages);
    }

    // Get pages by module (instructor view - all pages)
    // for future: add fold + unfold module feature
    public List<CoursePageRes> getPagesByModule(String moduleId) {
        return coursePageMapper.toResponseList(
                coursePageRepo.findByModule(moduleId)
        );
    }

    // Get published pages by module (student view)
    // for future: add fold + unfold module feature
    public List<CoursePageRes> getPublishedPagesByModule(String moduleId) {
        List<CoursePage> pages = coursePageRepo.findPublishedByModule(moduleId);

        // Filter by checking module.published
        List<CoursePage> visible = pages.stream()
                .filter(CoursePage::isVisibleToStudent)
                .collect(Collectors.toList());

        return coursePageMapper.toResponseList(visible);
    }



    //Publish page
    @Transactional
    public CoursePageRes publishPage(String pageId) {
        CoursePage page = coursePageRepo.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("CoursePage", pageId));

        page.setIsPublished(true);
        return coursePageMapper.toResponse(coursePageRepo.save(page));
    }

    //Unpublish page
    @Transactional
    public CoursePageRes unpublishPage(String pageId) {
        CoursePage page = coursePageRepo.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("CoursePage", pageId));

        page.setIsPublished(false);
        return coursePageMapper.toResponse(coursePageRepo.save(page));
    }

    //Delete page
    @Transactional
    public void deletePage(String pageId) {
        CoursePage page = coursePageRepo.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("CoursePage", pageId));

        coursePageRepo.delete(page);
    }
}
