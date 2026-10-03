package T_And_P.Training_and_Placement.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends mail events to Kafka so the API request does not wait for email.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPlannerEventPublisher implements PlannerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${tpms.kafka.planner-published-topic}")
    private String plannerPublishedTopic;

    @Value("${tpms.kafka.application-submitted-topic}")
    private String applicationSubmittedTopic;

    /**
     * Pushes PlannerPublishedEvent to Kafka. Mail is sent by the listener.
     */
    @Override
    public void publish(PlannerPublishedEvent event) {

        log.info(
                "publish() started for plannerId={}",
                event == null ? null : event.getPlannerId()
        );

        if (event == null) {
            log.info("publish() skipped because event is null");
            return;
        }

        try {
            String key = event.getPlannerId() == null
                    ? "planner"
                    : String.valueOf(event.getPlannerId());

            kafkaTemplate.send(plannerPublishedTopic, key, event);

            log.info(
                    "publish() completed for plannerId={}, topic={}",
                    event.getPlannerId(),
                    plannerPublishedTopic
            );

        } catch (Exception e) {
            log.error(
                    "publish() Kafka send failed for plannerId={}, mail will be skipped",
                    event.getPlannerId(),
                    e
            );
        }
    }

    /**
     * Pushes ApplicationSubmittedEvent to Kafka. Mail is sent by the listener.
     */
    @Override
    public void publishApplicationSubmitted(
            ApplicationSubmittedEvent event) {

        log.info(
                "publishApplicationSubmitted() started for email={}",
                event == null ? null : event.getEmail()
        );

        if (event == null) {
            log.info(
                    "publishApplicationSubmitted() skipped because event is null"
            );
            return;
        }

        try {
            String key = event.getEmail() == null
                    ? "application"
                    : event.getEmail();

            kafkaTemplate.send(
                    applicationSubmittedTopic,
                    key,
                    event
            );

            log.info(
                    "publishApplicationSubmitted() completed for email={}, topic={}",
                    event.getEmail(),
                    applicationSubmittedTopic
            );

        } catch (Exception e) {
            log.error(
                    "publishApplicationSubmitted() Kafka send failed for email={}, mail will be skipped",
                    event.getEmail(),
                    e
            );
        }
    }
}