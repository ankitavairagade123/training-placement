package T_And_P.Training_and_Placement.event;

import T_And_P.Training_and_Placement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for application activity mail.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationActivityEventListener {

    private final NotificationService notificationService;

    /**
     * Consumes application activity events and emails the student.
     */
    @KafkaListener(
            topics = "${tpms.kafka.application-activity-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onApplicationActivity(ApplicationActivityEvent event) {

        log.info(
                "onApplicationActivity() started for applicationId={}, status={}",
                event == null ? null : event.getApplicationId(),
                event == null ? null : event.getNewStatus()
        );

        try {

            if (event != null) {
                notificationService.sendApplicationActivityEmail(event);

                log.info(
                        "onApplicationActivity() completed for applicationId={}",
                        event.getApplicationId()
                );
            } else {
                log.warn("onApplicationActivity() skipped because event is null");
            }

        } catch (Exception e) {
            log.error(
                    "onApplicationActivity() failed for applicationId={}",
                    event == null ? null : event.getApplicationId(),
                    e
            );
        }
    }
}