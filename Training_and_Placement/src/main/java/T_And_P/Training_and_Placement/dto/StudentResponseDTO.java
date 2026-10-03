package T_And_P.Training_and_Placement.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponseDTO {

    private Long studentId;

    private String studentName;

    private String email;

    private Double sscPercentage;

    private Double hscPercentage;

    private Double ugCgpa;

    private Double attendance;

    private Integer activeBacklogs;

    private String branch;

    private Integer semester;

    private Integer passingYear;

    private String resumePath;
}