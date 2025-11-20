//package io.rubyxzzz.lms.backend.mapper;
//
//import io.rubyxzzz.lms.backend.dto.listItem.ApplicationList;
//import io.rubyxzzz.lms.backend.dto.response.ApplicationRes;
//import io.rubyxzzz.lms.backend.model.Application;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class ApplicationMapper {
//
//    /**
//     * Convert Application entity to detailed response DTO
//     */
//    public ApplicationRes toResponse(Application application) {
//        if (application == null) {
//            return null;
//        }
//
//        ApplicationRes response = new ApplicationRes();
//
//        // Copy all matching fields
//        BeanUtils.copyProperties(application, response);
//
//        // Set calculated fields
//        response.setFullName(application.getFullName());
//        response.setAge(application.getAge());
//        response.setIsMinor(application.isMinor());
//        response.setCanConvert(application.canConvertToStudent());
//
//        return response;
//    }
//
//    /**
//     * Convert Application entity to list item DTO (simplified)
//     */
//    public ApplicationList toListItem(Application application) {
//        if (application == null) {
//            return null;
//        }
//
//        ApplicationList item = new ApplicationList();
//
//        // Copy matching fields
//        BeanUtils.copyProperties(application, item);
//
//        // Set calculated field
//        item.setFullName(application.getFullName());
//
//        return item;
//    }
//
//    /**
//     * Convert list of Application entities to list of response DTOs
//     */
//    public List<ApplicationRes> toResponseList(List<Application> applications) {
//        if (applications == null) {
//            return List.of();
//        }
//
//        return applications.stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Convert list of Application entities to list of list item DTOs
//     */
//    public List<ApplicationList> toListItems(List<Application> applications) {
//        if (applications == null) {
//            return List.of();
//        }
//
//        return applications.stream()
//                .map(this::toListItem)
//                .collect(Collectors.toList());
//    }
//}