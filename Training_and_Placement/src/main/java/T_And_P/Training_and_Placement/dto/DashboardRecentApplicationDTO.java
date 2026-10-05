package T_And_P.Training_and_Placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRecentApplicationDTO {

    private Long applicationId;

    private Long studentId;

    private String studentName;

    private String plannerName;

    private String companyName;

    private String applicationStatus;

    private LocalDateTime appliedDate;
}