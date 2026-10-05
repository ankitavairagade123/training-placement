package T_And_P.Training_and_Placement.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * Kafka configuration for background mail processing.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${tpms.kafka.planner-published-topic}")
    private String plannerPublishedTopic;

    @Value("${tpms.kafka.application-submitted-topic}")
    private String applicationSubmittedTopic;

    @Value("${tpms.kafka.application-activity-topic}")
    private String applicationActivityTopic;

    /**
     * Kafka topic for planner-published events.
     */
    @Bean
    public NewTopic plannerPublishedTopic() {
        return TopicBuilder
                .name(plannerPublishedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Kafka topic for application-submitted events.
     */
    @Bean
    public NewTopic applicationSubmittedTopic() {
        return TopicBuilder
                .name(applicationSubmittedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Kafka topic for application activity events.
     */
    @Bean
    public NewTopic applicationActivityTopic() {
        return TopicBuilder
                .name(applicationActivityTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * KafkaTemplate used to publish background mail events.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }
}