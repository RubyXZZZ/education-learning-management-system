package io.rubyxzzz.lms.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileReq {
    private String phone;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
