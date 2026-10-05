package T_And_P.Training_and_Placement.event;

/**
 * Publishes background mail events.
 * Kafka is used as the transport mechanism.
 */
public interface PlannerEventPublisher {

    /**
     * Publishes planner-published mail work after a planner is made ACTIVE.
     */
    void publish(PlannerPublishedEvent event);

    /**
     * Publishes application-submitted mail work after a student applies.
     */
    void publishApplicationSubmitted(ApplicationSubmittedEvent event);

    /**
     * Publishes mail work after any application activity
     * such as applied, selected, rejected, or cancelled.
     */
    void publishApplicationActivity(ApplicationActivityEvent event);
}