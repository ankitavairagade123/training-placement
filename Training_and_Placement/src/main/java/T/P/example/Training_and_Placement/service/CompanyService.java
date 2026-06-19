package T.P.example.Training_and_Placement.service;

import T.P.example.Training_and_Placement.Entity.Company;
import T.P.example.Training_and_Placement.dto.CompanyRequestDTO;
import T.P.example.Training_and_Placement.dto.CompanyResponseDTO;
import T.P.example.Training_and_Placement.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    public CompanyResponseDTO saveCompany(CompanyRequestDTO requestDTO) {

        log.info("save company request received");

        if (requestDTO.getCompanyName() == null ||
                requestDTO.getCompanyName().trim().isEmpty()) {
            log.warn("company name is not provided for {}", requestDTO.getCompanyName());
            throw new RuntimeException("Company Name is required");
        }

        if (requestDTO.getAddress() == null ||
                requestDTO.getAddress().trim().isEmpty()) {
            log.warn("company address is not provided for {}" , requestDTO.getCompanyName());
            throw new RuntimeException("Address is required");
        }

        if (requestDTO.getPincode() == null ||
                requestDTO.getPincode().longValue() != 6) {
            log.warn("Invalid pincode for {}" , requestDTO.getCompanyName());
            throw new RuntimeException("Pincode must be 6 digits");
        }

        Company companyEntity = Company.builder()
                .companyName(requestDTO.getCompanyName())
                .address(requestDTO.getAddress())
                .pincode(requestDTO.getPincode())
                .build();

        log.info("Company is getting saved");
        Company savedCompany = companyRepository.save(companyEntity);

        log.info("company saved successfully ");

        return CompanyResponseDTO.builder()
                .id(savedCompany.getId())
                .companyName(savedCompany.getCompanyName())
                .address(savedCompany.getAddress())
                .pincode(savedCompany.getPincode())
                .build();
    }

    public List<CompanyResponseDTO> getAllCompanies() {

        log.info("fetching all companies");
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