package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.wdgg.model.userrole.UserRole;
import co.com.wdgg.model.userrole.gateways.UserRoleRepository;
import co.com.wdgg.r2dbc.entities.UserRoleEntity;
import reactor.core.publisher.Mono;

@Repository
public class UserRoleReactiveRepositoryAdapter implements UserRoleRepository {
    
    private final UserRoleReactiveRepository repository;

    public UserRoleReactiveRepositoryAdapter(UserRoleReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<UserRole> getUserRoleById(String id) {
        return repository.findById(UUID.fromString(id))
                .map(this::toDomain);
    }

    @Override
    public Mono<UserRole> getUserRoleByRole(String role) {
        return repository.findByRole(role)
                .map(this::toDomain);
    }

    private UserRole toDomain(UserRoleEntity userRoleEntity) {
        return new UserRole(
                userRoleEntity.getId(),
                userRoleEntity.getRole(),
                userRoleEntity.getDescription());
    }
}
