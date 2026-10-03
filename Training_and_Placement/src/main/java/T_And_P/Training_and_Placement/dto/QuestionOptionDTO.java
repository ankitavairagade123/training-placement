package T_And_P.Training_and_Placement.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionDTO {

    private Long optionId;

    private String optionText;

    private Integer displayOrder;
}