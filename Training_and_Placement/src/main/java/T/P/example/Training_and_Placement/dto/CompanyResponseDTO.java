package T.P.example.Training_and_Placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDTO {

    private Long id;
    private String companyName;
    private String address;
    private Long pincode;
}
