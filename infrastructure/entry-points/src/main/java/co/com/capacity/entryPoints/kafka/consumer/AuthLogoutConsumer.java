package co.com.capacity.entryPoints.kafka.consumer;

import co.com.capacity.usecase.deleteauth.DeleteAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthLogoutConsumer {

    private final DeleteAuthService deleteAuthService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.generic-auth-logout}")
    public void consume(String message) {
        try {
            AuthLogoutEvent event = objectMapper.readValue(message, AuthLogoutEvent.class);
            deleteAuthService.deleteByToken(event.getToken())
                    .subscribe(null, error -> log.error("Error deleting auth logout: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing auth logout message: {}", e.getMessage());
        }
    }

    private static class AuthLogoutEvent {
        private String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}