package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    private Mode mode;

    private Integer maxStudents;

    private Long companyId;

    private String status;

    private String venue;

    private String website;

    private List<PlannerDtlDTO> plannerDetails;



}
