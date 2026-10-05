package T_And_P.Training_and_Placement.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka payload for any student-application activity mail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationActivityEvent {

    private Long applicationId;

    private String email;

    private String studentName;

    private String plannerName;

    private String companyName;

    private String previousStatus;

    private String newStatus;
}