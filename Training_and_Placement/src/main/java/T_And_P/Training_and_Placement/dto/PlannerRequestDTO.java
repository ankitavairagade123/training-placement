package T_And_P.Training_and_Placement.dto;

import java.time.LocalDateTime;
import java.util.List;

import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlannerRequestDTO {

    private Long id;

    private String plannerName;

    private String plannerDesc;

    private PlannerType plannerType;

    private PlannerScheduleType plannerScheduleType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime registrationStartDate;

    private LocalDateTime registrationEndDate;

    private Mode mode;

    private Integer maxStudents;

    private Long companyId;

    private String status;

    private String venue;

    private String website;

    private String meetingLink;

    private String remarks;

    private String attachmentPath;

    private List<PlannerDtlDTO> plannerDetails;

    private List<PlannerQuestionDTO> questions;
}