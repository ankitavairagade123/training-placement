package T_And_P.Training_and_Placement.event;


import T_And_P.Training_and_Placement.service.NotificationService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener for planner-published mail.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlannerPublishedEventListener {

    private final NotificationService notificationService;

    /**
     * Consumes planner-published events and emails eligible students.
     */
    @KafkaListener(
            topics = "${tpms.kafka.planner-published-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPlannerPublished(PlannerPublishedEvent event) {

        log.info(
                "onPlannerPublished() started for plannerId={}",
                event == null ? null : event.getPlannerId()
        );

        try {
            if (event == null) {
                log.info("onPlannerPublished() skipped because event is null");
                return;
            }

            notificationService.sendPlannerPublishedEmails(event);

            log.info(
                    "onPlannerPublished() completed for plannerId={}",
                    event.getPlannerId()
            );

        } catch (Exception e) {
            log.error(
                    "onPlannerPublished() failed for plannerId={}",
                    event == null ? null : event.getPlannerId(),
                    e
            );
        }
    }
}
