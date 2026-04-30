package co.com.capacity.usecase.saveauth;

import co.com.capacity.model.auth.Auth;
import co.com.capacity.model.auth.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SaveAuthUseCase implements SaveAuthService {

    private final AuthRepository authRepository;

    @Override
    public Mono<Auth> save(Auth auth) {
        return authRepository.save(auth);
    }
}