package T_And_P.Training_and_Placement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApplicationHdrRequestDTO {

    private Long applicationId;

    private Long studentId;

    private Long plannerId;

    private List<PlacementApplicationDtlRequestDTO> applicationDetails;

    private String resumePath;
}