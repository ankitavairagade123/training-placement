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
public class DashboardUpcomingPlannerDTO {

    private Long plannerId;

    private String plannerName;

    private String companyName;

    private String plannerType;

    private String mode;

    private LocalDateTime registrationStartDate;

    private LocalDateTime registrationEndDate;
}