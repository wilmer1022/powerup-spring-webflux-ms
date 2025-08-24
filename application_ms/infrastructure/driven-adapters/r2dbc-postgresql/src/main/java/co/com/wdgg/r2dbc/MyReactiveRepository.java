package co.com.wdgg.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MyReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, UUID> {

    Flux<ApplicationEntity> findByUserDocumentNumber(String userDocumentNumber);
}
