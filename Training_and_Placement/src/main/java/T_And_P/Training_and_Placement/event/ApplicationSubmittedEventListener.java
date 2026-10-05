package T_And_P.Training_and_Placement.event;

import T_And_P.Training_and_Placement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for application-submitted mail.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationSubmittedEventListener {

    private final NotificationService notificationService;

    /**
     * Consumes application-submitted events and emails the student.
     */
    @KafkaListener(
            topics = "${tpms.kafka.application-submitted-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onApplicationSubmitted(ApplicationSubmittedEvent event) {

        log.info(
                "onApplicationSubmitted() started for email={}",
                event == null ? null : event.getEmail()
        );

        try {
            if (event != null) {

                notificationService.sendApplicationSubmittedEmail(
                        event.getEmail(),
                        event.getStudentName(),
                        event.getPlannerName()
                );

                log.info(
                        "onApplicationSubmitted() completed for email={}",
                        event.getEmail()
                );

            } else {
                log.warn("onApplicationSubmitted() skipped because event is null");
            }

        } catch (Exception e) {
            log.error(
                    "onApplicationSubmitted() failed for email={}",
                    event == null ? null : event.getEmail(),
                    e
            );
        }
    }
}