package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlannerRequestDTO {

    @NotBlank(message = "Planner name is required")
    private String plannerName;

    private String plannerDesc;

    @NotNull(message = "Planner type is required")
    private PlannerType plannerType;

    @NotNull(message = "Planner schedule type is required")
    private PlannerScheduleType plannerScheduleType;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "mode is required")
    private Mode node;

    @NotNull(message = "MaxStudents is required")
    private Integer maxStudents;

    @NotNull(message = "Company id is required")
    private Long companyId;
}
