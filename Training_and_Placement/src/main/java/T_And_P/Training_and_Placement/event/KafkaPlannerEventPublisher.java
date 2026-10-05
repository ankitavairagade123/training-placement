package T_And_P.Training_and_Placement.event;

import T_And_P.Training_and_Placement.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Sends mail events to Kafka.
 *
 * If Kafka send fails, mail is sent directly through NotificationService
 * so that the student still receives the notification.
 */
@Slf4j
@Component
public class KafkaPlannerEventPublisher implements PlannerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NotificationService notificationService;

    @Value("${tpms.kafka.planner-published-topic}")
    private String plannerPublishedTopic;

    @Value("${tpms.kafka.application-submitted-topic}")
    private String applicationSubmittedTopic;

    @Value("${tpms.kafka.application-activity-topic}")
    private String applicationActivityTopic;

    public KafkaPlannerEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            NotificationService notificationService) {

        this.kafkaTemplate = kafkaTemplate;
        this.notificationService = notificationService;

        log.info("KafkaPlannerEventPublisher initialized");
    }

    /**
     * Publishes PlannerPublishedEvent to Kafka.
     * If Kafka fails, sends planner mail directly.
     */
    @Override
    public void publish(final PlannerPublishedEvent event) {

        log.info(
                "publish() started, plannerId={}",
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

            ListenableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(
                            plannerPublishedTopic,
                            key,
                            event
                    );

            future.addCallback(
                    new ListenableFutureCallback<SendResult<String, Object>>() {

                        @Override
                        public void onSuccess(
                                SendResult<String, Object> result) {

                            log.info(
                                    "publish() completed, plannerId={}, topic={}",
                                    event.getPlannerId(),
                                    plannerPublishedTopic
                            );
                        }

                        @Override
                        public void onFailure(Throwable ex) {

                            log.error(
                                    "publish() Kafka failed, sending planner mail directly, plannerId={}",
                                    event.getPlannerId(),
                                    ex
                            );

                            sendPlannerMailDirectly(event);
                        }
                    }
            );

        } catch (Exception e) {

            log.error(
                    "publish() Kafka failed, sending planner mail directly, plannerId={}",
                    event.getPlannerId(),
                    e
            );

            sendPlannerMailDirectly(event);
        }
    }

    /**
     * Publishes ApplicationSubmittedEvent to Kafka.
     * If Kafka fails, sends application submitted mail directly.
     */
    @Override
    public void publishApplicationSubmitted(
            final ApplicationSubmittedEvent event) {

        log.info(
                "publishApplicationSubmitted() started, email={}",
                event == null ? null : event.getEmail()
        );

        if (event == null) {
            log.info(
                    "publishApplicationSubmitted() skipped because event is null"
            );
            return;
        }

        try {
            String key = StringUtils.hasText(event.getEmail())
                    ? event.getEmail()
                    : "application";

            ListenableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(
                            applicationSubmittedTopic,
                            key,
                            event
                    );

            future.addCallback(
                    new ListenableFutureCallback<SendResult<String, Object>>() {

                        @Override
                        public void onSuccess(
                                SendResult<String, Object> result) {

                            log.info(
                                    "publishApplicationSubmitted() completed, email={}, topic={}",
                                    event.getEmail(),
                                    applicationSubmittedTopic
                            );
                        }

                        @Override
                        public void onFailure(Throwable ex) {

                            log.error(
                                    "publishApplicationSubmitted() Kafka failed, "
                                            + "sending mail directly, email={}",
                                    event.getEmail(),
                                    ex
                            );

                            sendApplicationSubmittedMailDirectly(event);
                        }
                    }
            );

        } catch (Exception e) {

            log.error(
                    "publishApplicationSubmitted() Kafka failed, "
                            + "sending mail directly, email={}",
                    event.getEmail(),
                    e
            );

            sendApplicationSubmittedMailDirectly(event);
        }
    }

    /**
     * Publishes ApplicationActivityEvent to Kafka.
     * If Kafka fails, sends application activity mail directly.
     */
    @Override
    public void publishApplicationActivity(
            final ApplicationActivityEvent event) {

        log.info(
                "publishApplicationActivity() started, applicationId={}, status={}",
                event == null ? null : event.getApplicationId(),
                event == null ? null : event.getNewStatus()
        );

        if (event == null) {
            log.info(
                    "publishApplicationActivity() skipped because event is null"
            );
            return;
        }

        try {
            String key;

            if (event.getApplicationId() != null) {
                key = String.valueOf(event.getApplicationId());
            } else if (StringUtils.hasText(event.getEmail())) {
                key = event.getEmail();
            } else {
                key = "application";
            }

            ListenableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(
                            applicationActivityTopic,
                            key,
                            event
                    );

            future.addCallback(
                    new ListenableFutureCallback<SendResult<String, Object>>() {

                        @Override
                        public void onSuccess(
                                SendResult<String, Object> result) {

                            log.info(
                                    "publishApplicationActivity() completed, "
                                            + "applicationId={}, topic={}",
                                    event.getApplicationId(),
                                    applicationActivityTopic
                            );
                        }

                        @Override
                        public void onFailure(Throwable ex) {

                            log.error(
                                    "publishApplicationActivity() Kafka failed, "
                                            + "sending mail directly, applicationId={}",
                                    event.getApplicationId(),
                                    ex
                            );

                            sendApplicationActivityMailDirectly(event);
                        }
                    }
            );

        } catch (Exception e) {

            log.error(
                    "publishApplicationActivity() Kafka failed, "
                            + "sending mail directly, applicationId={}",
                    event.getApplicationId(),
                    e
            );

            sendApplicationActivityMailDirectly(event);
        }
    }

    /**
     * Sends planner notification directly when Kafka fails.
     */
    private void sendPlannerMailDirectly(
            PlannerPublishedEvent event) {

        try {
            notificationService.sendPlannerPublishedEmails(event);
        } catch (Exception e) {
            log.error(
                    "Direct planner mail failed, plannerId={}",
                    event.getPlannerId(),
                    e
            );
        }
    }

    /**
     * Sends application submitted notification directly when Kafka fails.
     */
    private void sendApplicationSubmittedMailDirectly(
            ApplicationSubmittedEvent event) {

        try {
            notificationService.sendApplicationSubmittedEmail(
                    event.getEmail(),
                    event.getStudentName(),
                    event.getPlannerName()
            );
        } catch (Exception e) {
            log.error(
                    "Direct application submitted mail failed, email={}",
                    event.getEmail(),
                    e
            );
        }
    }

    /**
     * Sends application activity notification directly when Kafka fails.
     */
    private void sendApplicationActivityMailDirectly(
            ApplicationActivityEvent event) {

        try {
            notificationService.sendApplicationActivityEmail(event);
        } catch (Exception e) {
            log.error(
                    "Direct application activity mail failed, applicationId={}",
                    event.getApplicationId(),
                    e
            );
        }
    }
}