package co.com.capacity.usecase.deletebootcampmatch;

import reactor.core.publisher.Mono;

import java.util.List;

public interface DeleteBootcampMatchService {
    Mono<Void> deleteMatch(Long bootcampId, List<Long> capacityIds);
}