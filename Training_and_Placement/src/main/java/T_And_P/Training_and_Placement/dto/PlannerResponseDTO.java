package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import T_And_P.Training_and_Placement.constant.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlannerResponseDTO {

    private Long id;

    private String plannerName;

    private String plannerDesc;

    private PlannerType plannerType;

    private PlannerScheduleType plannerScheduleType;

    private Status status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Mode mode;

    private Integer maxStudents;

    private Long companyId;

    private String companyName;
}
