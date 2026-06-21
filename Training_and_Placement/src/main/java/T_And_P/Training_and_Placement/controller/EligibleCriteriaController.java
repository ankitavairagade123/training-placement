package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.EligibleCriteriaRequestDTO;
import T_And_P.Training_and_Placement.dto.EligibleCriteriaResponseDTO;
import T_And_P.Training_and_Placement.service.EligibleCriteriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/eligible-criteria")
public class EligibleCriteriaController {


    private final EligibleCriteriaService service;

    @PostMapping("/save")
    public ResponseEntity<EligibleCriteriaResponseDTO> save(@RequestBody EligibleCriteriaRequestDTO requestDTO) {

        log.info("Save Eligible Criteria API called");

        return ResponseEntity.ok(
                service.saveEligibleCriteria(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EligibleCriteriaResponseDTO> getById(
            @PathVariable Long id) {

        log.info("Get Eligible Criteria by id API called");

        return ResponseEntity.ok(
                service.getById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<EligibleCriteriaResponseDTO>> getAll() {

        log.info("Get All Eligible Criteria API called");

        return ResponseEntity.ok(
                service.getAll());
    }

    @DeleteMapping("/getbyId/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {

        log.info("Delete Eligible Criteria API called");

        service.delete(id);

        return ResponseEntity.ok(
                "Eligible Criteria deleted successfully");
    }
}
