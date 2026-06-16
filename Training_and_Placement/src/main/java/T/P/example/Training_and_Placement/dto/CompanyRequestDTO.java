package T.P.example.Training_and_Placement.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDTO {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 50, message = "The company name between 2 to 50")
    private String companyName;

    @NotBlank(message = "Company name is required")
    private String address;

    private Integer pincode;
}
