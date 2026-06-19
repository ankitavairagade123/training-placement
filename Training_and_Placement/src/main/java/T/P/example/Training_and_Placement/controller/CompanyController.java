package T.P.example.Training_and_Placement.controller;

import T.P.example.Training_and_Placement.dto.CompanyRequestDTO;
import T.P.example.Training_and_Placement.dto.CompanyResponseDTO;
import T.P.example.Training_and_Placement.service.CompanyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger(CompanyController.class);

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> saveCompany(
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO) {

            log.info("save company request received");

        CompanyResponseDTO response = companyService.saveCompany(companyRequestDTO);

        log.info("company saved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {

        log.info("getting list of companies");
        List<CompanyResponseDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable Long id) {

        log.info("delete company request received");
        companyService.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }
}