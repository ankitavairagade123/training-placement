package T_And_P.Training_and_Placement.dto;


import T_And_P.Training_and_Placement.constant.ApplicationStatus;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationStatusRequestDTO {

    @NotNull(message = "{application.id.required}")
    private Long applicationId;

    @NotNull(message = "{application.status.required}")
    private ApplicationStatus applicationStatus;
}