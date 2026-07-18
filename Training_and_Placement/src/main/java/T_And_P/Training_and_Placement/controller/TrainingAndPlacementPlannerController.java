package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.PlannerRequestDTO;
import T_And_P.Training_and_Placement.dto.PlannerResponseDTO;
import T_And_P.Training_and_Placement.service.TrainingAndPlacementPlannerService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
@RestController
@RequestMapping("api/planner")
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
    @PostMapping("/save")
    public ResponseEntity<PlannerResponseDTO> savePlanner(@RequestBody PlannerRequestDTO plannerRequestDTO){

        log.info("save planner request received");


        PlannerResponseDTO response = plannerService.savePlanner(plannerRequestDTO);

        log.info("company saved successfully");
        return ResponseEntity.ok(response);
    }
}
