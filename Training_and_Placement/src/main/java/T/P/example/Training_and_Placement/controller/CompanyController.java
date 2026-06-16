package T.P.example.Training_and_Placement.controller;

import T.P.example.Training_and_Placement.Entity.Company;
import T.P.example.Training_and_Placement.dto.CompanyDTO;
import T.P.example.Training_and_Placement.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public Company saveCompany(
            @Valid @RequestBody CompanyDTO companyDTO) {
        Company company = Company.builder()
                .id(dto.getId())
                .companyName(dto.getCompanyName())
                .address(dto.getAddress())
                .pincode(dto.getPincode())
                .build();

        return companyRepository.save(company);;
    }

    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @DeleteMapping("/{id}")
    public String deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return "Company deleted successfully";
    }
}