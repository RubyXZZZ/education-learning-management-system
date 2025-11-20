package io.rubyxzzz.lms.backend.dto.request;

import io.rubyxzzz.lms.backend.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSessionReq {
    private LocalDate startDate;
    private LocalDate endDate;
}
