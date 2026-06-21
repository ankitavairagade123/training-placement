package T_And_P.Training_and_Placement.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleCriteriaResponseDTO {


    private Long id;

    private String eligibleName;

    private String status;
}
