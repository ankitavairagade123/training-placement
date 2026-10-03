package T_And_P.Training_and_Placement.dto;

import java.time.LocalDateTime;
import java.util.List;

import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import T_And_P.Training_and_Placement.constant.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlannerResponseDTO {

    private Long id;

    private String plannerName;

    private String plannerDesc;

    private PlannerType plannerType;

    private PlannerScheduleType plannerScheduleType;

    private Status status;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private LocalDateTime registrationStartDate;

    private LocalDateTime registrationEndDate;

    private Mode mode;

    private Integer maxStudents;

    private Long companyId;

    private String companyName;

    private String companyCode;

    private String startDate;

    private String startTimeDisplay;

    private String endDate;

    private String endTimeDisplay;

    private String venue;

    private String website;

    private String meetingLink;

    private String remarks;

    private String attachmentPath;

    private String publishedBy;

    private LocalDateTime publishedAt;

    private List<PlannerDtlDTO> plannerDetails;

    private List<PlannerQuestionDTO> questions;
}

