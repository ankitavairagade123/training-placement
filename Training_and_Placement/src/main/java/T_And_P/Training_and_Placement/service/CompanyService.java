package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.CompanyBean;
import T_And_P.Training_and_Placement.dto.CompanyRequestDTO;
import T_And_P.Training_and_Placement.dto.CompanyResponseDTO;
import T_And_P.Training_and_Placement.entity.CompanyMaster;
import T_And_P.Training_and_Placement.exception.CompanyException;
import T_And_P.Training_and_Placement.repository.CompanyRepository;
import io.swagger.v3.oas.models.info.Contact;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
@Service
@AllArgsConstructor
public class CompanyService {


    private final CompanyRepository companyRepository;

    private final MessageSource messageSource;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PINCODE_PATTERN =
            Pattern.compile("^[1-9][0-9]{5}$");

    private static final Pattern CONTACT_NUMBER_PATTERN =
            Pattern.compile("^[6-9]\\d{9}$");

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
                .company_name(requestDTO.getCompanyName())
                .address(requestDTO.getAddress())
                .pincode(requestDTO.getPincode())
                .contactNumber(requestDTO.getContactNumber())
                .website(requestDTO.getWebsite())
                .email(requestDTO.getEmail())
                .build();

        log.info("Company is getting saved");
        CompanyMaster savedCompany = companyRepository.save(companyEntity);

        log.info("company saved successfully ");

        return CompanyResponseDTO.builder()
                .id(savedCompany.getId())
                .companyName(savedCompany.getCompany_name())
                .address(savedCompany.getAddress())
                .pincode(savedCompany.getPincode())
                .website(savedCompany.getWebsite())
                .contactNumber(savedCompany.getContactNumber())
                .email(savedCompany.getEmail())
                .build();
    }

    public String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
    private void validateCompanyRequest(CompanyRequestDTO request) {

        validateRequired(request.getCompanyName(), "Company name is required");
        validateRequired(request.getAddress(), "Address is required");

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new CompanyException(getMessage("validation.email.required"),HttpStatus.BAD_REQUEST);
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new CompanyException("Invalid email format.",HttpStatus.BAD_REQUEST);
        }

        if (request.getPincode() == null || request.getPincode().toString().trim().isEmpty()) {
            throw new CompanyException("Pincode is required.",HttpStatus.BAD_REQUEST);
        }

        if (!PINCODE_PATTERN.matcher(request.getPincode().toString()).matches()) {
            throw new CompanyException("Invalid Indian pincode.",HttpStatus.BAD_REQUEST);
        }

        if(request.getContactNumber() == null || request.getContactNumber().isEmpty()){
            throw new CompanyException(getMessage("validation.contactNumber.required"),HttpStatus.BAD_REQUEST);
        }

        if (!CONTACT_NUMBER_PATTERN.matcher(request.getContactNumber()).matches()) {
            throw new CompanyException(
                    "Invalid Indian contact number.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new CompanyException(message,HttpStatus.BAD_REQUEST);
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
                            .website(company.getWebsite())
                            .contactNumber(company.getContactNumber())
                            .email(company.getEmail())
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
                .contactNumber(company.getContactNumber())
                .website(company.getWebsite())
                .email(company.getEmail())
                .build();
    }
}