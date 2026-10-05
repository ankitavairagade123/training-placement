package T_And_P.Training_and_Placement.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes background mail events to Kafka.
 * The API request does not wait for email processing.
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

    @Value("${tpms.kafka.application-activity-topic}")
    private String applicationActivityTopic;

    /**
     * Publishes PlannerPublishedEvent to Kafka.
     */
    @Override
    public void publish(PlannerPublishedEvent event) {
        log.info("publish() started, plannerId={}",
                event == null ? null : event.getPlannerId());

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
                    "publish() completed, plannerId={}, topic={}",
                    event.getPlannerId(),
                    plannerPublishedTopic
            );
        } catch (Exception e) {
            log.error(
                    "publish() Kafka send failed, plannerId={}, topic={}",
                    event.getPlannerId(),
                    plannerPublishedTopic,
                    e
            );
        }
    }

    /**
     * Publishes ApplicationSubmittedEvent to Kafka.
     */
    @Override
    public void publishApplicationSubmitted(ApplicationSubmittedEvent event) {
        log.info("publishApplicationSubmitted() started, email={}",
                event == null ? null : event.getEmail());

        if (event == null) {
            log.info("publishApplicationSubmitted() skipped because event is null");
            return;
        }

        try {
            String key = event.getEmail() == null
                    ? "application"
                    : event.getEmail();

            kafkaTemplate.send(applicationSubmittedTopic, key, event);

            log.info(
                    "publishApplicationSubmitted() completed, email={}, topic={}",
                    event.getEmail(),
                    applicationSubmittedTopic
            );
        } catch (Exception e) {
            log.error(
                    "publishApplicationSubmitted() Kafka send failed, email={}, topic={}",
                    event.getEmail(),
                    applicationSubmittedTopic,
                    e
            );
        }
    }

    /**
     * Publishes ApplicationActivityEvent to Kafka.
     */
    @Override
    public void publishApplicationActivity(ApplicationActivityEvent event) {
        log.info(
                "publishApplicationActivity() started, applicationId={}, status={}",
                event == null ? null : event.getApplicationId(),
                event == null ? null : event.getNewStatus()
        );

        if (event == null) {
            log.info("publishApplicationActivity() skipped because event is null");
            return;
        }

        try {
            String key = event.getApplicationId() == null
                    ? (event.getEmail() == null
                    ? "application"
                    : event.getEmail())
                    : String.valueOf(event.getApplicationId());

            kafkaTemplate.send(applicationActivityTopic, key, event);

            log.info(
                    "publishApplicationActivity() completed, applicationId={}, topic={}",
                    event.getApplicationId(),
                    applicationActivityTopic
            );
        } catch (Exception e) {
            log.error(
                    "publishApplicationActivity() Kafka send failed, applicationId={}, topic={}",
                    event.getApplicationId(),
                    applicationActivityTopic,
                    e
            );
        }
    }
}