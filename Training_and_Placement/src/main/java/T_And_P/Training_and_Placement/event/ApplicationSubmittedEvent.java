package T_And_P.Training_and_Placement.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka payload for application-submitted mail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSubmittedEvent {

    private String email;

    private String studentName;

    private String plannerName;
}