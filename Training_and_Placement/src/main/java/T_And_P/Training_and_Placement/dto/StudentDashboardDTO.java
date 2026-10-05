package T_And_P.Training_and_Placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDTO {

    private Long studentId;

    private String studentName;

    private String email;

    private long totalApplications;

    private long appliedCount;

    private long shortlistedCount;

    private long interviewScheduledCount;

    private long selectedCount;

    private long rejectedCount;

    private long cancelledCount;

    private long offerAcceptedCount;

    private List<DashboardRecentApplicationDTO> myApplications;

    private List<DashboardUpcomingPlannerDTO> upcomingPlanners;
}