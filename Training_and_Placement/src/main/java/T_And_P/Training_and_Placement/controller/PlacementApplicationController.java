package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.*;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.service.PlacementApplicationService;
import T_And_P.Training_and_Placement.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST APIs for the student apply flow, faculty status updates
 * and offer-letter upload.
 */
@Slf4j
@RestController
@RequestMapping("/api/placement-application")
@RequiredArgsConstructor
public class PlacementApplicationController {

    private final PlacementApplicationService placementApplicationService;
    private final MessageUtil messageUtil;

    /**
     * Student applies to a published planner.
     * System validates eligibility, registration window, resume and terms before saving.
     */
    @PostMapping("/apply")
    public PlacementApplicationHdrResponseDTO applyForDrive(
            @RequestBody PlacementApplicationHdrRequestDTO requestDTO) {

        log.info("applyForDrive() started for studentId={}, plannerId={}",
                requestDTO == null ? null : requestDTO.getStudentId(),
                requestDTO == null ? null : requestDTO.getPlannerId());

        try {
            PlacementApplicationHdrResponseDTO response =
                    placementApplicationService.applyForDrive(requestDTO);

            log.info("applyForDrive() completed for applicationId={}",
                    response.getId());

            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("applyForDrive() failed", e);
            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Pre-checks whether a student is eligible for a planner
     * without creating an application.
     */
    @GetMapping("/eligibility")
    public EligibilityCheckResponseDTO checkEligibility(
            @RequestParam Long studentId,
            @RequestParam Long plannerId) {

        log.info("checkEligibility() started for studentId={}, plannerId={}",
                studentId, plannerId);

        try {
            EligibilityCheckResponseDTO response =
                    placementApplicationService.checkEligibility(studentId, plannerId);

            log.info(
                    "checkEligibility() completed for studentId={}, plannerId={}, eligible={}",
                    studentId,
                    plannerId,
                    response.isEligible());

            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "checkEligibility() failed for studentId={}, plannerId={}",
                    studentId,
                    plannerId,
                    e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns all applications submitted by one student.
     */
    @GetMapping("/student/{studentId}")
    public List<PlacementApplicationHdrResponseDTO> getApplicationsByStudentId(
            @PathVariable Long studentId) {

        log.info("getApplicationsByStudentId() started for studentId={}", studentId);

        try {
            List<PlacementApplicationHdrResponseDTO> response =
                    placementApplicationService.getByStudentId(studentId);

            log.info(
                    "getApplicationsByStudentId() completed for studentId={}, count={}",
                    studentId,
                    response.size());

            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "getApplicationsByStudentId() failed for studentId={}",
                    studentId,
                    e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns all applications received for one planner
     * (faculty verification screen).
     */
    @GetMapping("/planner/{plannerId}")
    public List<PlacementApplicationHdrResponseDTO> getApplicationsByPlanner(
            @PathVariable Long plannerId) {

        log.info("getApplicationsByPlanner() started for plannerId={}", plannerId);

        try {
            List<PlacementApplicationHdrResponseDTO> response =
                    placementApplicationService.getApplicationsByPlannerId(plannerId);

            log.info(
                    "getApplicationsByPlanner() completed for plannerId={}, count={}",
                    plannerId,
                    response.size());

            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "getApplicationsByPlanner() failed for plannerId={}",
                    plannerId,
                    e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Faculty updates application status
     * (shortlisted, interview, selected, rejected, offer accepted).
     */
    @PutMapping("/status")
    public PlacementApplicationHdrResponseDTO updateStatus(
            @RequestBody UpdateApplicationStatusRequestDTO requestDTO) {

        log.info("updateStatus() started for applicationId={}, status={}",
                requestDTO == null ? null : requestDTO.getApplicationId(),
                requestDTO == null ? null : requestDTO.getApplicationStatus());

        try {
            PlacementApplicationHdrResponseDTO response =
                    placementApplicationService.updateApplicationStatus(requestDTO);

            log.info(
                    "updateStatus() completed for applicationId={}, status={}",
                    response.getId(),
                    response.getApplicationStatus());

            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("updateStatus() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * After a student accepts an offer, they can upload
     * offer letter / joining letter paths.
     */
    @PostMapping("/{applicationId}/offer-letter")
    public PlacementApplicationHdrResponseDTO uploadOfferLetter(
            @PathVariable Long applicationId,
            @RequestBody OfferLetterRequestDTO requestDTO) {

        log.info("uploadOfferLetter() started for applicationId={}", applicationId);

        try {
            PlacementApplicationHdrResponseDTO response =
                    placementApplicationService.uploadOfferLetter(
                            applicationId,
                            requestDTO);

            log.info(
                    "uploadOfferLetter() completed for applicationId={}",
                    applicationId);

            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "uploadOfferLetter() failed for applicationId={}",
                    applicationId,
                    e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes an application record.
     */
    @DeleteMapping("/{applicationId}")
    public String deleteApplication(@PathVariable Long applicationId) {

        log.info("deleteApplication() started for applicationId={}", applicationId);

        try {
            placementApplicationService.deleteApplication(applicationId);

            log.info(
                    "deleteApplication() completed for applicationId={}",
                    applicationId);

            return "Application deleted successfully";
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "deleteApplication() failed for applicationId={}",
                    applicationId,
                    e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}