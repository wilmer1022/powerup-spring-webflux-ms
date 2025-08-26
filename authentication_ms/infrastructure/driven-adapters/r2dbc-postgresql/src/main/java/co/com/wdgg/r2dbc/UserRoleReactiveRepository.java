package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.wdgg.r2dbc.entities.UserRoleEntity;
import reactor.core.publisher.Mono;

public interface UserRoleReactiveRepository extends ReactiveCrudRepository<UserRoleEntity, UUID> {
    
    Mono<UserRoleEntity> findByRole(String role);
}
