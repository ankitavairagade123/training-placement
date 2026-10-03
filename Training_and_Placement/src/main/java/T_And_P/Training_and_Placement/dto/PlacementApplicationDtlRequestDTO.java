package T_And_P.Training_and_Placement.dto;


import javax.validation.constraints.NotBlank;

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

    @NotBlank(message = "{application.field.name.required}")
    private String fieldName;

    @NotBlank(message = "{application.field.value.required}")
    private String fieldValue;
}