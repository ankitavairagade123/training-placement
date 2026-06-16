package T.P.example.Training_and_Placement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 50, message = "company name must be between 2 to 50")
    private String companyName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "pincode is required")
    @Min(value = 100000, message = "value must be 6 digits")
    @Max(value = 999999, message = "value must be 6 digits")
    private Integer pincode;


}
