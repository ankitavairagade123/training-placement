package T_And_P.Training_and_Placement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentRequestDTO {

    private Long studentId;

    @NotBlank(message = "student name is required")
    private String studentName;

}
