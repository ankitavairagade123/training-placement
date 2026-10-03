package T_And_P.Training_and_Placement.dto;


import java.util.List;

import T_And_P.Training_and_Placement.constant.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerQuestionDTO {

    private Long questionId;

    private String question;

    private FieldType fieldType;

    private Boolean mandatory;

    private List<QuestionOptionDTO> options;
}