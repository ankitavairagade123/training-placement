package T_And_P.Training_and_Placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApplicationHdrRequestDTO {

    private Long id;

    private Long studentId;

    private Long plannerId;

    private String resumePath;
}