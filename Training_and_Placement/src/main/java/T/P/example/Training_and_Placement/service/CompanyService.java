package T.P.example.Training_and_Placement.service;

import T.P.example.Training_and_Placement.Entity.Company;
import T.P.example.Training_and_Placement.dto.CompanyRequestDTO;
import T.P.example.Training_and_Placement.dto.CompanyResponseDTO;
import T.P.example.Training_and_Placement.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public CompanyResponseDTO saveCompany(CompanyRequestDTO requestDTO) {
        Company companyEntity = Company.builder()
                .companyName(requestDTO.getCompanyName())
                .address(requestDTO.getAddress())
                .pincode(requestDTO.getPincode())
                .build();

        Company savedCompany = companyRepository.save(companyEntity);

        return CompanyResponseDTO.builder()
                .id(savedCompany.getId())
                .companyName(savedCompany.getCompanyName())
                .address(savedCompany.getAddress())
                .pincode(savedCompany.getPincode())
                .build();
    }

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(company -> CompanyResponseDTO.builder()
                        .id(company.getId())
                        .companyName(company.getCompanyName())
                        .address(company.getAddress())
                        .pincode(company.getPincode())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}