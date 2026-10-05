package T_And_P.Training_and_Placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDashboardDTO {

    private Long companyId;

    private String companyName;

    private long plannerCount;

    private long appliedCount;

    private long shortlistedCount;

    private long interviewScheduledCount;

    private long selectedCount;

    private long rejectedCount;

    private long cancelledCount;

    private long offerAcceptedCount;
}