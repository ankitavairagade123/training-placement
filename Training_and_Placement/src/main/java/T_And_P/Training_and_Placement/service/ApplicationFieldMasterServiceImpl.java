package T_And_P.Training_and_Placement.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import T_And_P.Training_and_Placement.bean.ApplicationFieldProjection;
import T_And_P.Training_and_Placement.constant.FieldType;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.ApplicationFieldRequestDTO;
import T_And_P.Training_and_Placement.dto.ApplicationFieldResponseDTO;
import T_And_P.Training_and_Placement.entity.ApplicationFieldMaster;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.ApplicationFieldMasterRepository;
import T_And_P.Training_and_Placement.util.MapperUtil;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic for application field master
 * (extra form fields used on applications).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationFieldMasterServiceImpl {

    private final ApplicationFieldMasterRepository repository;
    private final MessageUtil messageUtil;

    /**
     * Creates or updates an application field. Field name must be unique.
     */
    public ApplicationFieldResponseDTO saveField(ApplicationFieldRequestDTO requestDTO) {

        log.info(
                "saveField() started for fieldId={}, fieldName={}",
                requestDTO == null ? null : requestDTO.getFieldId(),
                requestDTO == null ? null : requestDTO.getFieldName()
        );

        try {
            validateRequest(requestDTO);

            if (Objects.nonNull(requestDTO.getFieldId())) {

                log.info(
                        "Update request for Application Field id={}",
                        requestDTO.getFieldId()
                );

                repository.existsFieldById(requestDTO.getFieldId())
                        .orElseThrow(() ->
                                new PlacementApplicationException(
                                        messageUtil.badRequest("field.not.found"),
                                        HttpStatus.BAD_REQUEST
                                )
                        );

                repository.findDuplicateForUpdate(
                                requestDTO.getFieldName().trim(),
                                requestDTO.getFieldId()
                        )
                        .ifPresent(data -> {
                            throw new PlacementApplicationException(
                                    messageUtil.badRequest("field.name.exists"),
                                    HttpStatus.BAD_REQUEST
                            );
                        });

            } else {

                log.info("Create request for Application Field");

                repository.findByFieldNameIgnoreCase(
                                requestDTO.getFieldName().trim()
                        )
                        .ifPresent(data -> {
                            throw new PlacementApplicationException(
                                    messageUtil.badRequest("field.name.exists"),
                                    HttpStatus.BAD_REQUEST
                            );
                        });
            }

            ApplicationFieldMaster entity = ApplicationFieldMaster.builder()
                    .fieldId(requestDTO.getFieldId())
                    .fieldName(requestDTO.getFieldName().trim())
                    .fieldType(requestDTO.getFieldType())
                    .status(Status.valueOf(requestDTO.getStatus().trim()))
                    .build();

            log.info("Saving Application Field into database");

            ApplicationFieldMaster savedEntity = repository.save(entity);

            log.info(
                    "saveField() completed for fieldId={}",
                    savedEntity.getFieldId()
            );

            return getById(savedEntity.getFieldId());

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "saveField() failed due to unexpected error",
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("field.cannot.save"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Loads one application field by id.
     */
    public ApplicationFieldResponseDTO getById(Long id) {

        log.info("getById() started for fieldId={}", id);

        ApplicationFieldProjection projection =
                repository.getApplicationFieldByFieldId(id)
                        .orElseThrow(() ->
                                new PlacementApplicationException(
                                        messageUtil.badRequest("field.not.found"),
                                        HttpStatus.BAD_REQUEST
                                )
                        );

        ApplicationFieldResponseDTO response = toResponse(projection);

        log.info("getById() completed for fieldId={}", id);

        return response;
    }

    /**
     * Returns all application fields.
     */
    public List<ApplicationFieldResponseDTO> getAll() {

        log.info("getAll() started");

        List<ApplicationFieldResponseDTO> response =
                repository.getAllApplicationFields()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        log.info("getAll() completed, count={}", response.size());

        return response;
    }

    /**
     * Returns only ACTIVE application fields for form configuration.
     */
    public List<ApplicationFieldResponseDTO> getActiveFields() {

        log.info("getActiveFields() started");

        List<ApplicationFieldResponseDTO> response =
                repository.getApplicationFieldsByStatus(Status.ACTIVE.name())
                        .stream()
                        .filter(Objects::nonNull)
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        log.info("getActiveFields() completed, count={}", response.size());

        return response;
    }

    /**
     * Maps application field projection to API response.
     */
    private ApplicationFieldResponseDTO toResponse(
            ApplicationFieldProjection projection) {

        if (projection == null) {
            return null;
        }

        return ApplicationFieldResponseDTO.builder()
                .fieldId(projection.getFieldId())
                .fieldName(projection.getFieldName())
                .fieldType(
                        MapperUtil.parseEnum(
                                FieldType.class,
                                projection.getFieldType()
                        )
                )
                .status(projection.getStatus())
                .build();
    }

    /**
     * Validates field name, type and status before save.
     */
    private void validateRequest(ApplicationFieldRequestDTO requestDTO) {

        log.info("validateRequest() started");

        if (requestDTO == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("field.request.null"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!StringUtils.hasText(requestDTO.getFieldName())) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("field.name.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (requestDTO.getFieldName().trim().length() > 50) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("field.name.max.length"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (requestDTO.getFieldType() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("field.type.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!StringUtils.hasText(requestDTO.getStatus())) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("field.status.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info("validateRequest() completed");
    }
}