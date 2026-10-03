package T_And_P.Training_and_Placement.service;


import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import T_And_P.Training_and_Placement.bean.CompanyBean;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.CompanyRequestDTO;
import T_And_P.Training_and_Placement.dto.CompanyResponseDTO;
import T_And_P.Training_and_Placement.entity.CompanyMaster;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.CompanyRepository;
import T_And_P.Training_and_Placement.util.MapperUtil;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.AllArgsConstructor;

/**
 * Business logic for Company Master.
 * Validates company data, prevents duplicate company codes, and maps entity/DTO.
 */
@Service
@AllArgsConstructor
public class CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PINCODE_PATTERN =
            Pattern.compile("^[1-9][0-9]{5}$");

    private static final Pattern CONTACT_NUMBER_PATTERN =
            Pattern.compile("^[6-9]\\d{9}$");

    private final CompanyRepository companyRepository;
    private final MessageUtil messageUtil;

    /**
     * Creates a new company or updates an existing one when id is present.
     */
    public CompanyResponseDTO saveCompany(CompanyRequestDTO requestDTO) {

        log.info(
                "saveCompany() started for id={}, companyCode={}",
                requestDTO == null ? null : requestDTO.getId(),
                requestDTO == null ? null : requestDTO.getCompanyCode()
        );

        try {
            validateCompanyRequest(requestDTO);

            if (Objects.nonNull(requestDTO.getId())) {
                log.info(
                        "saveCompany() update path for id={}",
                        requestDTO.getId()
                );

                companyRepository.getByIdCompany(requestDTO.getId())
                        .orElseThrow(() ->
                                new PlacementApplicationException(
                                        messageUtil.badRequest("company.not.found"),
                                        HttpStatus.BAD_REQUEST
                                )
                        );
            }

            if (StringUtils.hasText(requestDTO.getCompanyCode())) {
                log.info(
                        "saveCompany() checking duplicate companyCode={}",
                        requestDTO.getCompanyCode()
                );

                companyRepository.findByCompanyCodeIgnoreCase(
                                requestDTO.getCompanyCode().trim()
                        )
                        .filter(existingId ->
                                requestDTO.getId() == null
                                        || !existingId.equals(requestDTO.getId())
                        )
                        .ifPresent(existingId -> {
                            log.info(
                                    "saveCompany() duplicate companyCode found for id={}",
                                    existingId
                            );

                            throw new PlacementApplicationException(
                                    messageUtil.badRequest("company.code.exists"),
                                    HttpStatus.BAD_REQUEST
                            );
                        });
            }

            CompanyMaster companyEntity = CompanyMaster.builder()
                    .id(requestDTO.getId())
                    .companyName(MapperUtil.trimToNull(requestDTO.getCompanyName()))
                    .companyCode(MapperUtil.trimToNull(requestDTO.getCompanyCode()))
                    .companyType(MapperUtil.trimToNull(requestDTO.getCompanyType()))
                    .industryType(MapperUtil.trimToNull(requestDTO.getIndustryType()))
                    .hrName(MapperUtil.trimToNull(requestDTO.getHrName()))
                    .address(MapperUtil.trimToNull(requestDTO.getAddress()))
                    .pincode(requestDTO.getPincode())
                    .contactNumber(MapperUtil.trimToNull(requestDTO.getContactNumber()))
                    .website(MapperUtil.trimToNull(requestDTO.getWebsite()))
                    .email(MapperUtil.trimToNull(requestDTO.getEmail()))
                    .status(
                            requestDTO.getStatus() == null
                                    ? Status.ACTIVE
                                    : requestDTO.getStatus()
                    )
                    .build();

            log.info("Saving Company into database");

            CompanyMaster savedCompany = companyRepository.save(companyEntity);

            log.info(
                    "saveCompany() completed for companyId={}",
                    savedCompany.getId()
            );

            return toResponse(savedCompany);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("saveCompany() failed due to unexpected error", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("company.cannot.save"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Validates mandatory company fields and Indian email/phone/pincode formats.
     */
    private void validateCompanyRequest(CompanyRequestDTO request) {

        log.info("validateCompanyRequest() started");

        if (request == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("company.request.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        validateRequired(
                request.getCompanyName(),
                "company.name.required"
        );

        validateRequired(
                request.getCompanyCode(),
                "company.code.required"
        );

        validateRequired(
                request.getCompanyType(),
                "company.type.required"
        );

        validateRequired(
                request.getIndustryType(),
                "company.industry.required"
        );

        validateRequired(
                request.getWebsite(),
                "company.website.required"
        );

        if (!StringUtils.hasText(request.getEmail())) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("validation.email.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail().trim()).matches()) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("validation.email.invalid"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getPincode() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("validation.pincode.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!PINCODE_PATTERN.matcher(request.getPincode().toString()).matches()) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("validation.pincode.invalid"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!StringUtils.hasText(request.getContactNumber())) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("validation.contactNumber.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!CONTACT_NUMBER_PATTERN.matcher(request.getContactNumber().trim()).matches()) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("validation.contactNumber.invalid"),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info("validateCompanyRequest() completed");
    }

    /**
     * Throws PlacementApplicationException when a required text field is blank.
     */
    private void validateRequired(String value, String messageKey) {

        log.debug("validateRequired() started for key={}", messageKey);

        if (MapperUtil.isBlank(value)) {
            log.info("validateRequired() failed for key={}", messageKey);

            throw new PlacementApplicationException(
                    messageUtil.badRequest(messageKey),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.debug("validateRequired() completed");
    }

    /**
     * Returns all companies mapped to response DTOs.
     */
    public List<CompanyResponseDTO> getAllCompanies() {

        log.info("getAllCompanies() started");

        List<CompanyBean> companyBeans = companyRepository.getAllCompany();

        if (CollectionUtils.isEmpty(companyBeans)) {
            log.info("getAllCompanies() completed with empty list");
            return Collections.emptyList();
        }

        List<CompanyResponseDTO> response = companyBeans.stream()
                .filter(Objects::nonNull)
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info(
                "getAllCompanies() completed, count={}",
                response.size()
        );

        return response;
    }

    /**
     * Deletes a company. Fails if the company does not exist or is still referenced.
     */
    public void deleteCompany(Long id) {

        log.info("deleteCompany() started for id={}", id);

        try {
            companyRepository.getByIdCompany(id)
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest("company.not.found"),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            companyRepository.deleteById(id);

            log.info("deleteCompany() completed for id={}", id);

        } catch (PlacementApplicationException e) {
            log.info(
                    "deleteCompany() failed for id={}, reason={}",
                    id,
                    e.getMessage()
            );
            throw e;

        } catch (Exception e) {
            log.error(
                    "deleteCompany() failed for id={} due to reference or DB error",
                    id,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("company.cannot.delete"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Loads one company by id.
     */
    public CompanyResponseDTO getByIdCompany(Long id) {

        log.info("getByIdCompany() started for id={}", id);

        CompanyBean company = companyRepository.getByIdCompanyDetails(id)
                .orElseThrow(() ->
                        new PlacementApplicationException(
                                messageUtil.badRequest("company.details.not.found"),
                                HttpStatus.BAD_REQUEST
                        )
                );

        log.info("getByIdCompany() completed for id={}", id);

        return toResponse(company);
    }

    /**
     * Maps a CompanyMaster entity to API response.
     */
    private CompanyResponseDTO toResponse(CompanyMaster company) {

        if (company == null) {
            return null;
        }

        log.debug(
                "toResponse(CompanyMaster) started for id={}",
                company.getId()
        );

        return CompanyResponseDTO.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .companyCode(company.getCompanyCode())
                .companyType(company.getCompanyType())
                .industryType(company.getIndustryType())
                .hrName(company.getHrName())
                .address(company.getAddress())
                .pincode(company.getPincode())
                .website(company.getWebsite())
                .contactNumber(company.getContactNumber())
                .email(company.getEmail())
                .status(
                        company.getStatus() == null
                                ? Status.ACTIVE
                                : company.getStatus()
                )
                .build();
    }

    /**
     * Maps a native-query projection to API response.
     */
    private CompanyResponseDTO toResponse(CompanyBean company) {

        if (company == null) {
            return null;
        }

        log.debug(
                "toResponse(CompanyBean) started for id={}",
                company.getId()
        );

        return CompanyResponseDTO.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .companyCode(company.getCompanyCode())
                .companyType(company.getCompanyType())
                .industryType(company.getIndustryType())
                .hrName(company.getHrName())
                .address(company.getAddress())
                .pincode(company.getPincode())
                .website(company.getWebsite())
                .contactNumber(company.getContactNumber())
                .email(company.getEmail())
                .status(parseStatus(company.getStatus()))
                .build();
    }

    /**
     * Converts a DB status string to enum. Defaults to ACTIVE when blank.
     */
    private Status parseStatus(String status) {

        log.debug("parseStatus() started for status={}", status);

        if (!StringUtils.hasText(status)) {
            return Status.ACTIVE;
        }

        return MapperUtil.parseEnum(
                Status.class,
                status,
                Status.ACTIVE
        );
    }
}