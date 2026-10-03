package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    @NotBlank(message = "{field.name.required}")
    private String fieldName;

    @NotNull(message = "{field.type.required}")
    private FieldType fieldType;

    @NotNull(message = "{field.status.required}")
    private String status;
}
