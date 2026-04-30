package co.com.capacity.model.auth;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Auth {
    private Long id;
    private String email;
    private String token;
    private Integer expiresIn;
    private LocalDateTime createdAt;
}