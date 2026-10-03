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

import T_And_P.Training_and_Placement.dto.CompanyRequestDTO;
import T_And_P.Training_and_Placement.dto.CompanyResponseDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.service.CompanyService;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST APIs for Company Master.
 */
@Slf4j
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final MessageUtil messageUtil;

    /**
     * Creates or updates a company.
     */
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> saveCompany(
            @RequestBody CompanyRequestDTO companyRequestDTO) {

        log.info(
                "saveCompany() started for id={}, companyName={}",
                companyRequestDTO == null
                        ? null
                        : companyRequestDTO.getId(),
                companyRequestDTO == null
                        ? null
                        : companyRequestDTO.getCompanyName()
        );

        try {
            CompanyResponseDTO response =
                    companyService.saveCompany(companyRequestDTO);

            log.info(
                    "saveCompany() completed for companyId={}",
                    response.getId()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("saveCompany() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Returns all companies.
     */
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {

        log.info("getAllCompanies() started");

        try {
            List<CompanyResponseDTO> companies =
                    companyService.getAllCompanies();

            log.info(
                    "getAllCompanies() completed, count={}",
                    companies == null ? 0 : companies.size()
            );

            return ResponseEntity.ok(companies);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("getAllCompanies() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Loads one company by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getByIdCompany(
            @PathVariable("id") Long id) {

        log.info(
                "getByIdCompany() started for id={}",
                id
        );

        try {
            CompanyResponseDTO company =
                    companyService.getByIdCompany(id);

            log.info(
                    "getByIdCompany() completed for id={}",
                    id
            );

            return ResponseEntity.ok(company);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "getByIdCompany() failed for id={}",
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
     * Deletes a company.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(
            @PathVariable("id") Long id) {

        log.info(
                "deleteCompany() started for id={}",
                id
        );

        try {
            companyService.deleteCompany(id);

            log.info(
                    "deleteCompany() completed for id={}",
                    id
            );

            return ResponseEntity.ok(
                    "Company deleted successfully"
            );

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "deleteCompany() failed for id={}",
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