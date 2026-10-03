package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.PlannerRequestDTO;
import T_And_P.Training_and_Placement.dto.PlannerResponseDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.service.TrainingAndPlacementPlannerService;
import T_And_P.Training_and_Placement.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST APIs for planner create, publish, reject and student-visible active list.
 * A planner can be Campus Placement, Internship, Workshop, Industrial Visit, Seminar or Hackathon.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/planner")
public class TrainingAndPlacementPlannerController {

    private static final Logger log =
            LoggerFactory.getLogger(TrainingAndPlacementPlannerController.class);

    private final TrainingAndPlacementPlannerService plannerService;
    private final MessageUtil messageUtil;

    /**
     * Returns all planners for the TPO/Faculty planner grid (draft, active, rejected).
     */
    @GetMapping
    public ResponseEntity<List<PlannerResponseDTO>> getAllPlanners() {
        log.info("getAllPlanners() started");

        try {
            List<PlannerResponseDTO> planners = plannerService.getAllPlanners();

            log.info("getAllPlanners() completed, count={}", planners.size());

            return ResponseEntity.ok(planners);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("getAllPlanners() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Returns published planners that are currently open for student registration.
     */
    @GetMapping("/active")
    public ResponseEntity<List<PlannerResponseDTO>> getActivePlanner() {
        log.info("getActivePlanner() started");

        try {
            List<PlannerResponseDTO> planners = plannerService.getActivePlanners();

            log.info("getActivePlanner() completed, count={}", planners.size());

            return ResponseEntity.ok(planners);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("getActivePlanner() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Loads planner header, eligibility rules and questions for view/edit.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlannerResponseDTO> getPlannerById(
            @PathVariable Long id
    ) {
        log.info("getPlannerById() started for id={}", id);

        try {
            PlannerResponseDTO planner = plannerService.getPlannerById(id);

            log.info(
                    "getPlannerById() completed for id={}, status={}",
                    id,
                    planner.getStatus()
            );

            return ResponseEntity.ok(planner);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("getPlannerById() failed for id={}", id, e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Creates or updates a draft planner along with eligibility and questions.
     */
    @PostMapping
    public ResponseEntity<PlannerResponseDTO> savePlanner(
            @RequestBody PlannerRequestDTO plannerRequestDTO
    ) {
        log.info(
                "savePlanner() started for id={}, plannerName={}",
                plannerRequestDTO == null ? null : plannerRequestDTO.getId(),
                plannerRequestDTO == null ? null : plannerRequestDTO.getPlannerName()
        );

        try {
            PlannerResponseDTO response =
                    plannerService.savePlanner(plannerRequestDTO);

            log.info(
                    "savePlanner() completed for plannerId={}",
                    response.getId()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("savePlanner() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Publishes a draft planner after all validations pass.
     * publishedBy is taken from X-User-Name header; defaults to TPO.
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<PlannerResponseDTO> publishPlanner(
            @PathVariable Long id,
            @RequestHeader(
                    value = "X-User-Name",
                    required = false,
                    defaultValue = "TPO"
            ) String publishedBy
    ) {
        log.info(
                "publishPlanner() started for id={}, publishedBy={}",
                id,
                publishedBy
        );

        try {
            PlannerResponseDTO response =
                    plannerService.publishPlanner(id, publishedBy);

            log.info(
                    "publishPlanner() completed for id={}, status={}",
                    id,
                    response.getStatus()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("publishPlanner() failed for id={}", id, e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Marks a draft planner as REJECTED from the Publish Planner screen.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<PlannerResponseDTO> rejectPlanner(
            @PathVariable Long id
    ) {
        log.info("rejectPlanner() started for id={}", id);

        try {
            PlannerResponseDTO response = plannerService.rejectPlanner(id);

            log.info(
                    "rejectPlanner() completed for id={}, status={}",
                    id,
                    response.getStatus()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("rejectPlanner() failed for id={}", id, e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Deletes a non-published planner.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlanner(
            @PathVariable Long id
    ) {
        log.info("deletePlanner() started for id={}", id);

        try {
            plannerService.deletePlanner(id);

            log.info("deletePlanner() completed for id={}", id);

            return ResponseEntity.ok("Planner deleted successfully");

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("deletePlanner() failed for id={}", id, e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}