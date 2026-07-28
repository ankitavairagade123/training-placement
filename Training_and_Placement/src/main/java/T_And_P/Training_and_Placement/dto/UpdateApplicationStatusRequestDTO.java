package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationStatusRequestDTO {

    @NotNull(message = "application is is required")
    private Long applicationId;

    @NotNull(message = "application status is required")
    private ApplicationStatus applicationStatus;
}
