package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.ApplicationFieldProjection;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.ApplicationFieldRequestDTO;
import T_And_P.Training_and_Placement.dto.ApplicationFieldResponseDTO;
import T_And_P.Training_and_Placement.entity.ApplicationFieldMaster;
import T_And_P.Training_and_Placement.exception.CompanyException;
import T_And_P.Training_and_Placement.repository.ApplicationFieldMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationFieldMasterServiceImpl {

    private final ApplicationFieldMasterRepository repository;


    public ApplicationFieldResponseDTO saveField(
            ApplicationFieldRequestDTO requestDTO) {

        log.info("Save Application Field request received");

        validateRequest(requestDTO);

        if (Objects.nonNull(requestDTO.getFieldId())) {

            log.info("Update request for Application Field id : {}",
                    requestDTO.getFieldId());

            repository.findByFieldId(requestDTO.getFieldId())
                    .orElseThrow(() -> new CompanyException(
                            "Application Field not found",
                            HttpStatus.BAD_REQUEST));

            repository.findDuplicateForUpdate(
                            requestDTO.getFieldName().trim(),
                            requestDTO.getFieldId())
                    .ifPresent(data -> {
                        throw new CompanyException(
                                "Field Name already exists",
                                HttpStatus.BAD_REQUEST);
                    });

        } else {

            log.info("Create request for Application Field");

            repository.findByFieldNameIgnoreCase(
                            requestDTO.getFieldName().trim())
                    .ifPresent(data -> {
                        throw new CompanyException(
                                "Field Name already exists",
                                HttpStatus.BAD_REQUEST);
                    });
        }

        ApplicationFieldMaster entity = ApplicationFieldMaster.builder()
                .fieldId(requestDTO.getFieldId())
                .fieldName(requestDTO.getFieldName().trim())
                .fieldType(requestDTO.getFieldType())
                .status(Status.valueOf(requestDTO.getStatus()))
                .build();

        log.info("Saving Application Field into database");

        ApplicationFieldMaster savedEntity = repository.save(entity);

        log.info("Application Field saved successfully with id : {}",
                savedEntity.getFieldId());

        return ApplicationFieldResponseDTO.builder()
                .fieldId(savedEntity.getFieldId())
                .fieldName(savedEntity.getFieldName())
                .fieldType(savedEntity.getFieldType())
                .status(savedEntity.getStatus().toString())
                .build();
    }

    public ApplicationFieldResponseDTO getById(Long id) {

        log.info("Fetching Application Field by id : {}", id);

        ApplicationFieldProjection projection =
                repository.getApplicationFieldByFieldId(id)
                        .orElseThrow(() -> new CompanyException(
                                "Application Field not found",
                                HttpStatus.BAD_REQUEST));

        return ApplicationFieldResponseDTO.builder()
                .fieldId(projection.getFieldId())
                .fieldName(projection.getFieldName())
                .fieldType(projection.getFieldType())
                .status(projection.getStatus())
                .build();
    }

    public List<ApplicationFieldResponseDTO> getAll() {

        log.info("Fetching all Application Fields");

        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {

        log.info("Delete request received for id : {}", id);

        repository.findByFieldId(id)
                .orElseThrow(() -> new CompanyException(
                        "Application Field not found",
                        HttpStatus.BAD_REQUEST));

        repository.deleteById(id);

        log.info("Application Field deleted successfully");
    }

    private ApplicationFieldResponseDTO convertToDTO(
            ApplicationFieldMaster entity) {

        return ApplicationFieldResponseDTO.builder()
                .fieldId(entity.getFieldId())
                .fieldName(entity.getFieldName())
                .fieldType(entity.getFieldType())
                .status(entity.getStatus().toString())
                .build();
    }

    private void validateRequest(ApplicationFieldRequestDTO requestDTO) {

        if (requestDTO == null) {
            throw new CompanyException(
                    "Request cannot be null",
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(requestDTO.getFieldName())) {
            throw new CompanyException(
                    "Field Name is mandatory",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestDTO.getFieldName().trim().length() > 50) {
            throw new CompanyException(
                    "Field Name cannot exceed 50 characters",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestDTO.getFieldType() == null) {
            throw new CompanyException(
                    "Field Type is mandatory",
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(requestDTO.getStatus())) {
            throw new CompanyException(
                    "Status is mandatory",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public List<ApplicationFieldResponseDTO> getActiveFields() {

        log.info("Fetching all active application fields");

        return repository.findByStatus(Status.ACTIVE)
                .stream()
                .map(field -> ApplicationFieldResponseDTO.builder()
                        .fieldId(field.getFieldId())
                        .fieldName(field.getFieldName())
                        .fieldType(field.getFieldType())
                        .status(field.getStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }
}