package T_And_P.Training_and_Placement.event;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerPublishedEvent {

    private Long plannerId;

    private String plannerName;

    private String plannerType;

    private String companyName;

    private String companyCode;

    private LocalDateTime registrationStartDate;

    private LocalDateTime registrationEndDate;

    private LocalDateTime publishedAt;

    private String publishedBy;

    private List<String> eligibleStudentEmails;
}