package co.com.wdgg.usecase.userrole;

import co.com.wdgg.model.userrole.gateways.UserRoleRepository;
import co.com.wdgg.model.userrole.UserRole;
import reactor.core.publisher.Mono;

public class UserRoleUseCase {

    private final UserRoleRepository userRoleRepository;

    public UserRoleUseCase(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public Mono<UserRole> getUserRoleById(String id) {
        return userRoleRepository.getUserRoleById(id);
    }

    public Mono<UserRole> getUserRoleByRole(String role) {
        return userRoleRepository.getUserRoleByRole(role);
    }
}
