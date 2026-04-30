package co.com.capacity.entryPoints.kafka.consumer;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.usecase.createtechnologycatalog.CreateTechnologyCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TechnologyCatalogConsumer {

    private final CreateTechnologyCatalogService createTechnologyCatalogService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.sync-technology-catalog}")
    public void consume(String message) {
        try {
            TechnologyCatalogEvent event = objectMapper.readValue(message, TechnologyCatalogEvent.class);
            TechnologyCatalog technologyCatalog = TechnologyCatalog.builder()
                    .externalId(event.getId())
                    .name(event.getName())
                    .createdAt(LocalDateTime.now())
                    .build();
            createTechnologyCatalogService.create(technologyCatalog)
                    .subscribe(null, error -> log.error("Error creating technology catalog: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing technology catalog message: {}", e.getMessage());
        }
    }

    private static class TechnologyCatalogEvent {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}