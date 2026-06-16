package T.P.example.Training_and_Placement.service;

import T.P.example.Training_and_Placement.Entity.Company;
import T.P.example.Training_and_Placement.dto.CompanyDTO;
import T.P.example.Training_and_Placement.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company saveCompany(CompanyDTO companyDTO) {
        Company company = Company.builder()
                .companyName(companyDTO.getCompanyName())
                .address(companyDTO.getAddress())
                .pincode(companyDTO.getPincode())
                .build();

        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}
