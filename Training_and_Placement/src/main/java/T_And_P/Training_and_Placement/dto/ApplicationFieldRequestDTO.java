package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.FieldType;
import T_And_P.Training_and_Placement.constant.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationFieldRequestDTO {

    private Long fieldId;

    @NotBlank
    private String fieldName;

    @NotNull
    private FieldType fieldType;

    @NotNull
    private String status;
}