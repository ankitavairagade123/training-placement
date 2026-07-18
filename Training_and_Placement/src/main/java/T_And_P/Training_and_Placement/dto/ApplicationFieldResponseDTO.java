package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.FieldType;
import T_And_P.Training_and_Placement.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFieldResponseDTO {

    private Long fieldId;

    private String fieldName;

    private FieldType fieldType;

    private String status;
}