package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import T_And_P.Training_and_Placement.constant.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlannerResponseDTO {

    private Long id;

    private String plannerName;

    private String plannerDesc;

    private PlannerType plannerType;

    private PlannerScheduleType plannerScheduleType;

    private Status status;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Mode mode;

    private Long maxStudents;

    private Long companyId;

    private String companyName;

    private String startDate;

    private String startTimeDisplay;

    private String endDate;

    private String endTimeDisplay;

}
