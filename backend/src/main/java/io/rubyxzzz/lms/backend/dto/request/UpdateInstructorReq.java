package io.rubyxzzz.lms.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInstructorReq {
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String department;
    private String officeHours;
}
