package T.P.example.Training_and_Placement.controller;

import T.P.example.Training_and_Placement.dto.CompanyRequestDTO;
import T.P.example.Training_and_Placement.dto.CompanyResponseDTO;
import T.P.example.Training_and_Placement.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> saveCompany(
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO) {

        CompanyResponseDTO response = companyService.saveCompany(companyRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        List<CompanyResponseDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }
}