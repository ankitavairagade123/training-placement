package T_And_P.Training_and_Placement.dto;


import T_And_P.Training_and_Placement.constant.CriteriaRule;
import T_And_P.Training_and_Placement.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerDtlDTO {

    private Long id;

    private CriteriaRule criteriaRule;

    private Status status;
}
