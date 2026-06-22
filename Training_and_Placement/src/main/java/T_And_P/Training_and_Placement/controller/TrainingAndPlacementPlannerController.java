package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.PlannerResponseDTO;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import T_And_P.Training_and_Placement.service.TrainingAndPlacementPlannerService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/planner")
public class TrainingAndPlacementPlannerController {

    private static final Logger log = LoggerFactory.getLogger(TrainingAndPlacementPlannerController.class);

    private final TrainingAndPlacementPlannerService plannerService;

    public TrainingAndPlacementPlannerController(TrainingAndPlacementPlannerService plannerService) {
        this.plannerService = plannerService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<TrainingAndPlacementPlannerHdr>>
        getActivePlanner() {

           log.info("Fetching active planners");

           return ResponseEntity.ok(
                   plannerService.getActivePlanner());
    }
}
