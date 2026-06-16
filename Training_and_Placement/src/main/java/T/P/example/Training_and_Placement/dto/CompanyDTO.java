package T.P.example.Training_and_Placement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(min = 2,max = 50,
            message = "company name must be between 2 to 50")
    private String companyName;

    @NotBlank(message = "Address is required")
    private String address;

    private Integer pincode;

}
