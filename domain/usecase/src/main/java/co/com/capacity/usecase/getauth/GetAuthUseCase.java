package co.com.capacity.usecase.getauth;

import co.com.capacity.model.auth.Auth;
import co.com.capacity.model.auth.LoggedUser;
import co.com.capacity.model.auth.gateways.AuthRepository;
import co.com.capacity.model.utils.exception.UnauthorizedException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneOffset;

@RequiredArgsConstructor
public class GetAuthUseCase implements GetAuthService {

    private final AuthRepository authRepository;

    @Override
    public Mono<LoggedUser> getByToken(String token) {
        return authRepository.findByToken(token)
                .filter(this::isTokenValid)
                .map(auth -> new LoggedUser(auth.getEmail()))
                .switchIfEmpty(Mono.error(
                        new UnauthorizedException(GlobalExceptionEnum.UNAUTHORIZED)));
    }

    private boolean isTokenValid(Auth auth) {
        if (auth.getExpiresIn() == null) return true;
        long now = Instant.now().getEpochSecond();
        long created = auth.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        return now - created < auth.getExpiresIn();
    }
}