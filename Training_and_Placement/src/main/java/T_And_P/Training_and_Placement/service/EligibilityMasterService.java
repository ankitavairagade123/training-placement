package T_And_P.Training_and_Placement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import T_And_P.Training_and_Placement.bean.EligibilityBean;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.EligibilityRequestDTO;
import T_And_P.Training_and_Placement.dto.EligibilityResponseDTO;
import T_And_P.Training_and_Placement.entity.EligibilityMaster;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.EligibilityMasterRepository;
import T_And_P.Training_and_Placement.util.MapperUtil;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic for Eligibility Master.
 * These types (SSC, HSC, Attendance, etc.) are later configured
 * on a planner with a value and condition.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EligibilityMasterService {

    private final EligibilityMasterRepository eligibilityMasterRepository;
    private final MessageUtil messageUtil;

    /**
     * Creates or updates an eligibility type. Type name must be unique.
     */
    public EligibilityResponseDTO save(EligibilityRequestDTO request) {

        log.info(
                "save() started for id={}, eligibilityType={}",
                request == null ? null : request.getId(),
                request == null ? null : request.getEligibilityType()
        );

        try {
            validate(request);

            EligibilityMaster entity;

            if (request.getId() != null) {

                log.info(
                        "save() update path for id={}",
                        request.getId()
                );

                eligibilityMasterRepository
                        .existsEligibilityById(request.getId())
                        .orElseThrow(() ->
                                new PlacementApplicationException(
                                        messageUtil.badRequest("eligibility.not.found"),
                                        HttpStatus.BAD_REQUEST
                                )
                        );

                if (eligibilityMasterRepository
                        .existsByEligibilityTypeIgnoreCaseAndIdNot(
                                request.getEligibilityType().trim(),
                                request.getId()
                        )
                        .isPresent()) {

                    log.info(
                            "save() duplicate eligibilityType found during update"
                    );

                    throw new PlacementApplicationException(
                            messageUtil.badRequest("eligibility.already.exists"),
                            HttpStatus.BAD_REQUEST
                    );
                }

                entity = eligibilityMasterRepository
                        .getReferenceById(request.getId());

            } else {

                log.info(
                        "save() create path for eligibilityType={}",
                        request.getEligibilityType()
                );

                eligibilityMasterRepository
                        .findByEligibilityTypeIgnoreCase(
                                request.getEligibilityType().trim()
                        )
                        .ifPresent(existing -> {
                            log.info(
                                    "save() duplicate eligibilityType found during create"
                            );

                            throw new PlacementApplicationException(
                                    messageUtil.badRequest("eligibility.already.exists"),
                                    HttpStatus.BAD_REQUEST
                            );
                        });

                entity = new EligibilityMaster();
            }

            entity.setEligibilityType(
                    request.getEligibilityType().trim()
            );

            entity.setStatus(
                    request.getStatus() == null
                            ? Status.ACTIVE
                            : request.getStatus()
            );

            EligibilityMaster saved =
                    eligibilityMasterRepository.save(entity);

            log.info(
                    "save() completed for eligibilityId={}",
                    saved.getId()
            );

            return getById(saved.getId());

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "save() failed due to unexpected error",
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("eligibility.cannot.save"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Returns all eligibility types for the master screen.
     */
    public List<EligibilityResponseDTO> getAll() {

        log.info("getAll() started");

        List<EligibilityResponseDTO> response =
                eligibilityMasterRepository
                        .getAllEligibility()
                        .stream()
                        .filter(entity -> entity != null)
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        log.info(
                "getAll() completed, count={}",
                response.size()
        );

        return response;
    }

    /**
     * Returns only ACTIVE types. Planner creation must use this list.
     */
    public List<EligibilityResponseDTO> getActive() {

        log.info("getActive() started");

        List<EligibilityResponseDTO> response =
                eligibilityMasterRepository
                        .getEligibilityByStatus(Status.ACTIVE.name())
                        .stream()
                        .filter(entity -> entity != null)
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        log.info(
                "getActive() completed, count={}",
                response.size()
        );

        return response;
    }

    /**
     * Loads one eligibility type by id.
     */
    public EligibilityResponseDTO getById(Long id) {

        log.info(
                "getById() started for id={}",
                id
        );

        EligibilityBean entity =
                eligibilityMasterRepository
                        .getEligibilityById(id)
                        .orElseThrow(() ->
                                new PlacementApplicationException(
                                        messageUtil.badRequest("eligibility.not.found"),
                                        HttpStatus.BAD_REQUEST
                                )
                        );

        log.info(
                "getById() completed for id={}",
                id
        );

        return toResponse(entity);
    }

    /**
     * Deletes an eligibility type from master.
     */
    public void delete(Long id) {

        log.info(
                "delete() started for id={}",
                id
        );

        try {
            eligibilityMasterRepository
                    .existsEligibilityById(id)
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest("eligibility.not.found"),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            eligibilityMasterRepository.deleteById(id);

            log.info(
                    "delete() completed for id={}",
                    id
            );

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "delete() failed for id={} due to reference or DB error",
                    id,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("eligibility.cannot.delete"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Ensures eligibility type name is present.
     */
    private void validate(EligibilityRequestDTO request) {

        log.info("validate() started");

        if (request == null
                || !StringUtils.hasText(request.getEligibilityType())) {

            log.info(
                    "validate() failed: eligibility type is required"
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("eligibility.type.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info("validate() completed");
    }

    /**
     * Maps eligibility projection to API response.
     */
    private EligibilityResponseDTO toResponse(EligibilityBean entity) {

        if (entity == null) {
            return null;
        }

        log.debug(
                "toResponse() started for id={}",
                entity.getId()
        );

        return EligibilityResponseDTO.builder()
                .id(entity.getId())
                .eligibilityType(entity.getEligibilityType())
                .status(
                        MapperUtil.parseEnum(
                                Status.class,
                                entity.getStatus(),
                                Status.ACTIVE
                        )
                )
                .build();
    }
}