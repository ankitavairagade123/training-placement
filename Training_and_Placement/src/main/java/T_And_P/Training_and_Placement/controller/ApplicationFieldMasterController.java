package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.ApplicationFieldRequestDTO;
import T_And_P.Training_and_Placement.dto.ApplicationFieldResponseDTO;
import T_And_P.Training_and_Placement.service.ApplicationFieldMasterServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application-field")
@RequiredArgsConstructor
public class ApplicationFieldMasterController {

    private final ApplicationFieldMasterServiceImpl service;

    @PostMapping("/save")
    public ResponseEntity<ApplicationFieldResponseDTO> saveField(
            @Valid @RequestBody ApplicationFieldRequestDTO dto) {

        return ResponseEntity.ok(service.saveField(dto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ApplicationFieldResponseDTO>> getAllFields() {

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{fieldId}")
    public ResponseEntity<ApplicationFieldResponseDTO> getById(
            @PathVariable Long fieldId) {
        return ResponseEntity.ok(service.getById(fieldId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ApplicationFieldResponseDTO>> getActiveFields() {
        return ResponseEntity.ok(service.getActiveFields());
    }
}