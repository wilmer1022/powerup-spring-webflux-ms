package co.com.wdgg.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    Mono<UserEntity> findByDocumentNumber(String documentNumber);

    Mono<UserEntity> findByEmail(String email);
}
