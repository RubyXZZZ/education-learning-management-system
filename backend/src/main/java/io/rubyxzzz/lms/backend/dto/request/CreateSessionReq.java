package io.rubyxzzz.lms.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSessionReq {
    @NotBlank(message = "Session code is required")
    private String sessionCode;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

//
//    @NotNull(message = "Registration open date is required")
//    private LocalDate registrationOpenDate;
//
//    @NotNull(message = "Registration deadline is required")
//    private LocalDate registrationDeadline;
//
//
//    @NotNull(message = "Add/drop deadline is required")
//    private LocalDate addDropDeadline;
//
//    @NotNull(message = "Withdraw deadline is required")
//    private LocalDate withdrawDeadline;
}
