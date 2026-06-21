package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.EligibleCriteriaProjection;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.EligibleCriteriaRequestDTO;
import T_And_P.Training_and_Placement.dto.EligibleCriteriaResponseDTO;
import T_And_P.Training_and_Placement.entity.EligibleCritriaMaster;
import T_And_P.Training_and_Placement.exception.CompanyException;
import T_And_P.Training_and_Placement.repository.EligibleCriteriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EligibleCriteriaService {


    private final EligibleCriteriaRepository repository;


    public EligibleCriteriaResponseDTO saveEligibleCriteria(
            EligibleCriteriaRequestDTO requestDTO) {

        log.info("Save Eligible Criteria request received");

        validateRequest(requestDTO);

        if (Objects.nonNull(requestDTO.getId())) {

            log.info("Update request for eligible criteria id : {}",
                    requestDTO.getId());

            repository.getByIdEligible(requestDTO.getId())
                    .orElseThrow(() -> new CompanyException(
                            "Eligible Criteria not found",
                            HttpStatus.BAD_REQUEST));

            repository.findDuplicateForUpdate(
                            requestDTO.getEligibleName().trim(),
                            requestDTO.getId())
                    .ifPresent(data -> {
                        throw new CompanyException(
                                "Eligible name already exists",
                                HttpStatus.BAD_REQUEST);
                    });

        } else {

            log.info("Create request for eligible criteria");

            repository.findByEligibleName(
                            requestDTO.getEligibleName().trim())
                    .ifPresent(data -> {
                        throw new CompanyException(
                                "Eligible name already exists",
                                HttpStatus.BAD_REQUEST);
                    });
        }

        EligibleCritriaMaster entity = EligibleCritriaMaster.builder()
                .id(requestDTO.getId())
                .eligibleName(requestDTO.getEligibleName().trim())
                .status(Status.valueOf(requestDTO.getStatus()))
                .build();

        log.info("Saving eligible criteria into database");

        EligibleCritriaMaster savedEntity =
                repository.save(entity);

        log.info("Eligible criteria saved successfully with id : {}",
                savedEntity.getId());

        return EligibleCriteriaResponseDTO.builder()
                .id(savedEntity.getId())
                .eligibleName(savedEntity.getEligibleName())
                .status(savedEntity.getStatus().toString())
                .build();
    }


    public EligibleCriteriaResponseDTO getById(Long id) {

        log.info("Fetching eligible criteria by id : {}", id);

        EligibleCriteriaProjection projection =
                repository.getEligibleCriteriaById(id)
                        .orElseThrow(() -> new CompanyException(
                                "Eligible Criteria not found",
                                HttpStatus.BAD_REQUEST));

        return EligibleCriteriaResponseDTO.builder()
                .id(projection.getId())
                .eligibleName(projection.getEligibleName())
                .status(
                        projection.getStatus())
                .build();
    }


    public List<EligibleCriteriaResponseDTO> getAll() {

        log.info("Fetching all eligible criteria");

        return repository.getAllEligibleCriteria()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public void delete(Long id) {

        log.info("Delete request received for id : {}", id);

        EligibleCritriaMaster entity =
                repository.getByIdEligible(id)
                        .orElseThrow(() -> new CompanyException(
                                "Eligible Criteria not found",
                                HttpStatus.BAD_REQUEST));

        repository.delete(entity);

        log.info("Eligible criteria deleted successfully");
    }

    private EligibleCriteriaResponseDTO convertToDTO(
            EligibleCriteriaProjection projection) {

        return EligibleCriteriaResponseDTO.builder()
                .id(projection.getId())
                .eligibleName(projection.getEligibleName())
                .status(
                        projection.getStatus())
                .build();
    }

    private void validateRequest(
            EligibleCriteriaRequestDTO requestDTO) {

        if (requestDTO == null) {
            throw new CompanyException(
                    "Request cannot be null",
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(requestDTO.getEligibleName())) {
            throw new CompanyException(
                    "Eligible Name is mandatory",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestDTO.getEligibleName().trim().length() > 50) {
            throw new CompanyException(
                    "Eligible Name cannot exceed 50 characters",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestDTO.getStatus() == null) {
            throw new CompanyException(
                    "Status is mandatory",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
