package T_And_P.Training_and_Placement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import T_And_P.Training_and_Placement.dto.EligibilityRequestDTO;
import T_And_P.Training_and_Placement.dto.EligibilityResponseDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.service.EligibilityMasterService;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST APIs for Eligibility Master.
 * Stores eligibility types (SSC, HSC, Attendance, etc.)
 * that can later be attached to a planner.
 */
@Slf4j
@RestController
@RequestMapping("/api/eligibilityType")
@RequiredArgsConstructor
public class EligibilityTypeController {

    private final EligibilityMasterService eligibilityMasterService;
    private final MessageUtil messageUtil;

    /**
     * Creates or updates an eligibility type.
     * Only ACTIVE types can be selected while creating a planner.
     */
    @PostMapping
    public ResponseEntity<EligibilityResponseDTO> save(
            @RequestBody EligibilityRequestDTO requestDTO) {

        log.info(
                "save() started for eligibilityType={}, id={}",
                requestDTO == null
                        ? null
                        : requestDTO.getEligibilityType(),
                requestDTO == null
                        ? null
                        : requestDTO.getId()
        );

        try {
            EligibilityResponseDTO response =
                    eligibilityMasterService.save(requestDTO);

            log.info(
                    "save() completed for eligibilityId={}",
                    response.getId()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("save() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Returns every eligibility type, including inactive ones,
     * for the master screen.
     */
    @GetMapping
    public ResponseEntity<List<EligibilityResponseDTO>> getAll() {

        log.info("getAll() started");

        try {
            List<EligibilityResponseDTO> response =
                    eligibilityMasterService.getAll();

            log.info(
                    "getAll() completed, count={}",
                    response.size()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("getAll() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Returns only ACTIVE eligibility types
     * for the planner eligibility dropdown.
     */
    @GetMapping("/active")
    public ResponseEntity<List<EligibilityResponseDTO>> getActive() {

        log.info("getActive() started");

        try {
            List<EligibilityResponseDTO> response =
                    eligibilityMasterService.getActive();

            log.info(
                    "getActive() completed, count={}",
                    response.size()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("getActive() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Loads one eligibility type by id for view/edit.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EligibilityResponseDTO> getById(
            @PathVariable Long id) {

        log.info(
                "getById() started for id={}",
                id
        );

        try {
            EligibilityResponseDTO response =
                    eligibilityMasterService.getById(id);

            log.info(
                    "getById() completed for id={}",
                    id
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "getById() failed for id={}",
                    id,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Deletes an eligibility type from master.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {

        log.info(
                "delete() started for id={}",
                id
        );

        try {
            eligibilityMasterService.delete(id);

            log.info(
                    "delete() completed for id={}",
                    id
            );

            return ResponseEntity.ok(
                    "Eligibility type deleted successfully"
            );

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "delete() failed for id={}",
                    id,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}