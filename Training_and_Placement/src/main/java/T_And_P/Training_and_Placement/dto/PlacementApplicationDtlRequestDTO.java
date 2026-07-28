package T_And_P.Training_and_Placement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApplicationDtlRequestDTO {

    private Long applicationDetailId;

    @NotBlank(message = "field name is required")
    private String fieldName;

    @NotBlank(message = "field value is required")
    private String fieldValue;
}
