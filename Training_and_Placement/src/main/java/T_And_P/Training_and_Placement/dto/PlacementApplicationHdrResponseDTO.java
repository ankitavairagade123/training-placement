package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApplicationHdrResponseDTO {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long plannerId;

    private String plannerName;

    private String companyName;

    private String resumePath;

    private LocalDateTime appliedDate;

    private ApplicationStatus applicationStatus;
}