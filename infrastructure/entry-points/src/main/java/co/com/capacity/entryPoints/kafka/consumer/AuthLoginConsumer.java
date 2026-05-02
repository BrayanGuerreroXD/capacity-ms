package co.com.capacity.entryPoints.kafka.consumer;

import co.com.capacity.model.auth.Auth;
import co.com.capacity.usecase.saveauth.SaveAuthService;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthLoginConsumer {

    private final SaveAuthService saveAuthService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.auth-login-admin}")
    public void consume(String message) {
        try {
            AuthLoginEvent event = objectMapper.readValue(message, AuthLoginEvent.class);
            Auth auth = Auth.builder()
                    .email(event.getEmail())
                    .token(event.getToken())
                    .expiresIn(event.getExpiresIn())
                    .createdAt(LocalDateTime.now())
                    .build();
            saveAuthService.saveWithEmailValidation(auth)
                    .subscribe(null, error -> log.error("Error saving auth login: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing auth login message: {}", e.getMessage());
        }
    }

    private static class AuthLoginEvent {
        private String email;
        private String token;
        private Integer expiresIn;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public Integer getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Integer expiresIn) { this.expiresIn = expiresIn; }
    }
}