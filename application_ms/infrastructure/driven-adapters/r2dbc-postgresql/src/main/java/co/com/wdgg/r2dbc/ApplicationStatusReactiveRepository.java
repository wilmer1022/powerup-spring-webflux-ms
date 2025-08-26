package co.com.wdgg.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.wdgg.r2dbc.entities.ApplicationStatusEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface ApplicationStatusReactiveRepository extends ReactiveCrudRepository<ApplicationStatusEntity, UUID> {

    Mono<ApplicationStatusEntity> findByStatus(String status);
}
