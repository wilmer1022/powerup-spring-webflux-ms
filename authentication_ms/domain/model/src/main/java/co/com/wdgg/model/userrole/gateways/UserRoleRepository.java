package co.com.wdgg.model.userrole.gateways;

import co.com.wdgg.model.userrole.UserRole;
import reactor.core.publisher.Mono;

public interface UserRoleRepository {

    Mono<UserRole> getUserRoleById(String id);

    Mono<UserRole> getUserRoleByRole(String role);
}
