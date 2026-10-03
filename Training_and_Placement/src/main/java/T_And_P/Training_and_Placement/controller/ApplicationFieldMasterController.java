package T_And_P.Training_and_Placement.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import T_And_P.Training_and_Placement.dto.ApplicationFieldRequestDTO;
import T_And_P.Training_and_Placement.dto.ApplicationFieldResponseDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.service.ApplicationFieldMasterServiceImpl;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST APIs for application field master.
 * These fields can be used as extra questions or form fields on an application.
 */
@Slf4j
@RestController
@RequestMapping("/application-field")
@RequiredArgsConstructor
public class ApplicationFieldMasterController {

    private final ApplicationFieldMasterServiceImpl service;
    private final MessageUtil messageUtil;

    /**
     * Creates or updates an application field definition.
     */
    @PostMapping("/save")
    public ResponseEntity<ApplicationFieldResponseDTO> saveField(
            @Valid @RequestBody ApplicationFieldRequestDTO dto) {

        log.info(
                "saveField() started for fieldId={}, fieldName={}",
                dto == null ? null : dto.getFieldId(),
                dto == null ? null : dto.getFieldName()
        );

        try {
            ApplicationFieldResponseDTO response = service.saveField(dto);

            log.info(
                    "saveField() completed for fieldId={}",
                    response.getFieldId()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("saveField() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Returns all application fields.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ApplicationFieldResponseDTO>> getAllFields() {

        log.info("getAllFields() started");

        try {
            List<ApplicationFieldResponseDTO> response = service.getAll();

            log.info(
                    "getAllFields() completed, count={}",
                    response.size()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("getAllFields() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Loads one application field by id.
     */
    @GetMapping("/{fieldId}")
    public ResponseEntity<ApplicationFieldResponseDTO> getById(
            @PathVariable Long fieldId) {

        log.info(
                "getById() started for fieldId={}",
                fieldId
        );

        try {
            ApplicationFieldResponseDTO response =
                    service.getById(fieldId);

            log.info(
                    "getById() completed for fieldId={}",
                    fieldId
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "getById() failed for fieldId={}",
                    fieldId,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Returns only ACTIVE application fields for form configuration.
     */
    @GetMapping("/active")
    public ResponseEntity<List<ApplicationFieldResponseDTO>> getActiveFields() {

        log.info("getActiveFields() started");

        try {
            List<ApplicationFieldResponseDTO> response =
                    service.getActiveFields();

            log.info(
                    "getActiveFields() completed, count={}",
                    response.size()
            );

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("getActiveFields() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}