package T_And_P.Training_and_Placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacementApplicationDtlResponseDTO {

    private Long applicationDetailId;

    private String fieldName;

    private String fieldValue;
}
