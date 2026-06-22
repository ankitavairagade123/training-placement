package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.PlannerHdrBean;
import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.PlannerResponseDTO;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TrainingAndPlacementPlannerService {

    private final TrainingAndPlacementPlannerHdrRepository plannerHdrRepository;


    public List<PlannerResponseDTO> getActivePlanners() {

        log.info("Fetching active planners");

        List<PlannerHdrBean> projections = plannerHdrRepository.getActivePlanners();

        if (CollectionUtils.isEmpty(projections)) {

            log.info("No active planners found");

            return Collections.emptyList();
        }

        log.info("Total active planners found : {}", projections.size());

        return projections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Projection to DTO
     */
    private PlannerResponseDTO convertToResponse(PlannerHdrBean projection) {

        return PlannerResponseDTO.builder()
                .id(projection.getId())
                .plannerName(projection.getPlannerName())
                .plannerDesc(projection.getPlannerDesc())
                .plannerType(PlannerType.valueOf(projection.getPlannerType()))
                .mode(Mode.valueOf(projection.getMode()))
                .plannerScheduleType(PlannerScheduleType.valueOf(projection.getPlannerScheduleType()))
                .status(Status.valueOf(projection.getStatus()))
                .startTime(projection.getStartTime())
                .endTime(projection.getEndTime())
                .maxStudents(projection.getMaxStudents())
                .build();
    }
}
