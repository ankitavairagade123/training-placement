package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApplicationHdrResponseDTO {

    private Long id;

    private Long studentId;

    private String studentName;

    private String email;

    private Long plannerId;

    private String plannerName;

    private String companyName;

    private String resumePath;

    private Boolean termsAccepted;

    private String offerLetterPath;

    private String joiningLetterPath;

    private LocalDateTime appliedDate;

    private ApplicationStatus applicationStatus;

    private List<PlacementApplicationDtlResponseDTO> applicationDetails;
}