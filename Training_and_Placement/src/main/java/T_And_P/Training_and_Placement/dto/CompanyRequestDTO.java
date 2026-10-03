package T_And_P.Training_and_Placement.dto;

import T_And_P.Training_and_Placement.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDTO {

    private Long id;

    private String companyName;

    private String companyCode;

    private String companyType;

    private String industryType;

    private String hrName;

    private String address;

    private Long pincode;

    private String website;

    private String email;

    private String contactNumber;

    private Status status;
}
