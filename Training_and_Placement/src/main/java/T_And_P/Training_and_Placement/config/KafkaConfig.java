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
 * Kafka topics used for background mail processing.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${tpms.kafka.planner-published-topic}")
    private String plannerPublishedTopic;

    @Value("${tpms.kafka.application-submitted-topic}")
    private String applicationSubmittedTopic;

    /**
     * Topic for planner-published emails.
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
     * Topic for application-submitted emails.
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
     * Producer used by KafkaPlannerEventPublisher.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}