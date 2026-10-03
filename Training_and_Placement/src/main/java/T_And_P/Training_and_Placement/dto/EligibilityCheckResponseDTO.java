package T_And_P.Training_and_Placement.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityCheckResponseDTO {

    private Long studentId;

    private Long plannerId;

    private boolean eligible;

    private List<String> reasons;
}