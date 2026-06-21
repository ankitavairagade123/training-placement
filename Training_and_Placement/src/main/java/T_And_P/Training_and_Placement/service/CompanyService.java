package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.CompanyBean;
import T_And_P.Training_and_Placement.dto.CompanyRequestDTO;
import T_And_P.Training_and_Placement.dto.CompanyResponseDTO;
import T_And_P.Training_and_Placement.entity.CompanyMaster;
import T_And_P.Training_and_Placement.exception.CompanyException;
import T_And_P.Training_and_Placement.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {


    private final CompanyRepository companyRepository;

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    public CompanyResponseDTO saveCompany(CompanyRequestDTO requestDTO) {

        log.info("save company request received");

        validateCompanyRequest(requestDTO);
        if (Objects.nonNull(requestDTO.getId())) {
            companyRepository.getByIdCompany(requestDTO.getId())
                    .orElseThrow(() -> new CompanyException("Company not found", HttpStatus.BAD_REQUEST));
        }

        CompanyMaster companyEntity = CompanyMaster.builder()
                .id(requestDTO.getId())
                .companyName(requestDTO.getCompanyName())
                .address(requestDTO.getAddress())
                .pincode(requestDTO.getPincode())
                .build();

        log.info("Company is getting saved");
        CompanyMaster savedCompany = companyRepository.save(companyEntity);

        log.info("company saved successfully ");

        return CompanyResponseDTO.builder()
                .id(savedCompany.getId())
                .companyName(savedCompany.getCompanyName())
                .address(savedCompany.getAddress())
                .pincode(savedCompany.getPincode())
                .build();
    }

    private void validateCompanyRequest(CompanyRequestDTO requestDTO) {

        validateRequired(requestDTO.getCompanyName(), "Company name is required");
        validateRequired(requestDTO.getAddress(), "Address is required");

        Long pincode = requestDTO.getPincode();
        if (pincode == null || String.valueOf(pincode).length() != 6) {
            throw new CompanyException("Pincode must be 6 digits", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public List<CompanyResponseDTO> getAllCompanies() {

        log.info("fetching all companies");

        List<CompanyBean> companyBeans = companyRepository.getAllCompany();
        if (!CollectionUtils.isEmpty(companyBeans)) {
            return companyBeans.stream()
                    .map(company -> CompanyResponseDTO.builder()
                            .id(company.getId())
                            .companyName(company.getCompanyName())
                            .address(company.getAddress())
                            .pincode(company.getPincode())
                            .build())
                    .collect(Collectors.toList());
        }
        return null;
    }

    public void deleteCompany(Long id) {
        try {
            companyRepository.getByIdCompany(id)
                    .orElseThrow(() -> new CompanyException("Company not found", HttpStatus.BAD_REQUEST));
            companyRepository.deleteById(id);
        } catch (Exception e) {
            throw new CompanyException("Company can't delete ", HttpStatus.BAD_REQUEST);
        }

    }

    public CompanyResponseDTO getByIdCompany(Long id) {

        CompanyBean company = companyRepository.getByIdCompanyDetails(id)
                .orElseThrow(() -> new CompanyException("Company details not found", HttpStatus.BAD_REQUEST));


        return CompanyResponseDTO.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .address(company.getAddress())
                .pincode(company.getPincode())
                .build();
    }
}