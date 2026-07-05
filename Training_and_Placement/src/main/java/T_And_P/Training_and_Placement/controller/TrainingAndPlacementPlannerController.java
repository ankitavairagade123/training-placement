package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.PlannerResponseDTO;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import T_And_P.Training_and_Placement.service.TrainingAndPlacementPlannerService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
@RestController("/api/planner")
public class TrainingAndPlacementPlannerController {

    private static final Logger log = LoggerFactory.getLogger(TrainingAndPlacementPlannerController.class);

    private final TrainingAndPlacementPlannerService plannerService;

    /**
     * Fetch all active planners
     */
    @GetMapping("/active")
    public ResponseEntity<List<PlannerResponseDTO>> getActivePlanner() {

           log.info("Fetching active planners");

           return ResponseEntity.ok(plannerService.getActivePlanners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlannerResponseDTO> getPlannerById(
            @PathVariable Long id) {

        log.info("Get planner by id request received");

        return ResponseEntity.ok(
                plannerService.getPlannerById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<PlannerResponseDTO>> getAllPlanners() {

        log.info("Get all planners request received");

        return ResponseEntity.ok(
                plannerService.getAllPlanners());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePlanner(
            @PathVariable Long id) {

        plannerService.deletePlanner(id);

        return ResponseEntity.ok(
                "Planner deleted successfully");
    }
}
