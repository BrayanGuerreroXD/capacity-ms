package co.com.capacity.entryPoints.kafka.consumer;

import co.com.capacity.usecase.deletetechnologycatalog.DeleteTechnologyCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TechnologyDeletedConsumer {

    private final DeleteTechnologyCatalogService deleteTechnologyCatalogService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.sync-technology-deleted}")
    public void consume(String message) {
        try {
            TechnologyDeletedEvent event = objectMapper.readValue(message, TechnologyDeletedEvent.class);
            deleteTechnologyCatalogService.deleteByExternalId(event.getId())
                    .subscribe(null, error -> log.error("Error deleting technology catalog: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing technology deleted message: {}", e.getMessage());
        }
    }

    private static class TechnologyDeletedEvent {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}