package co.com.capacity.usecase.deleteauth;

import co.com.capacity.model.auth.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteAuthUseCase implements DeleteAuthService {

    private final AuthRepository authRepository;

    @Override
    public Mono<Void> deleteByToken(String token) {
        return authRepository.deleteByToken(token);
    }
}