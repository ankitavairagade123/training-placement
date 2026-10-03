package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityResponseDTO {

    private Long id;

    private String eligibilityType;

    private Status status;
}
