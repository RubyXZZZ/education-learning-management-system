package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.listItem.AdminList;
import io.rubyxzzz.lms.backend.dto.response.AdminRes;
import io.rubyxzzz.lms.backend.model.Admin;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminMapper {

    public AdminRes toResponse(Admin admin) {
        if (admin == null) return null;

        AdminRes response = new AdminRes();
        BeanUtils.copyProperties(admin, response);

        response.setFullName(admin.getFullName());

        return response;
    }

    public AdminList toListItem(Admin admin) {
        if (admin == null) return null;

        return AdminList.builder()
                .id(admin.getId())
                .employeeNumber(admin.getEmployeeNumber())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .department(admin.getDepartment())
                .position(admin.getPosition())
                .isSuperAdmin(admin.isSuperAdmin())
                .status(admin.getStatus())
                .build();
    }

    public List<AdminRes> toResponseList(List<Admin> admins) {
        if (admins == null) return List.of();
        return admins.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AdminList> toListItems(List<Admin> admins) {
        if (admins == null) return List.of();
        return admins.stream().map(this::toListItem).collect(Collectors.toList());
    }
}
