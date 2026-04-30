package co.com.capacity.config;

import co.com.capacity.drivenAdapters.kafka.publisher.CapacityEventPublisherAdapter;
import co.com.capacity.drivenAdapters.kafka.publisher.CapacityTechnologyEventPublisherAdapter;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyEventGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class UseCasesConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.sync-capacity-catalog}")
    private String syncCapacityCatalog;

    @Value("${kafka.topics.sync-capacity-deleted}")
    private String syncCapacityDeleted;

    @Value("${kafka.topics.sync-capacity-technology-catalog}")
    private String syncCapacityTechnologyCatalog;

    @Value("${kafka.topics.sync-capacity-technology-deleted}")
    private String syncCapacityTechnologyDeleted;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public CapacityEventGateway capacityEventGateway(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new CapacityEventPublisherAdapter(kafkaTemplate, objectMapper, syncCapacityCatalog, syncCapacityDeleted);
    }

    @Bean
    public CapacityTechnologyEventGateway capacityTechnologyEventGateway(KafkaTemplate<String, Object> kafkaTemplate) {
        return new CapacityTechnologyEventPublisherAdapter(kafkaTemplate, syncCapacityTechnologyCatalog, syncCapacityTechnologyDeleted);
    }
}