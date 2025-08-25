package co.com.wdgg.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository interface for managing UserEntity objects.
 * This repository extends ReactiveCrudRepository for non-blocking database operations
 * and provides additional custom methods for finding users.
 *
 * @author wilmer1022
 * @version 0.0.1
 * @since 2023-08-25
 */
public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    /**
     * Finds a user by their document number.
     * This method returns a Mono, which represents a single result or no result
     * in a reactive, non-blocking way.
     *
     * @param documentNumber The unique document number of the user.
     * @return A {@link Mono} containing the found {@link UserEntity} or an empty Mono if not found.
     */
    Mono<UserEntity> findByDocumentNumber(String documentNumber);

    /**
     * Finds a user by their email address.
     *
     * @param email The unique email address of the user.
     * @return A {@link Mono} containing the found {@link UserEntity} or an empty Mono if not found.
     */
    Mono<UserEntity> findByEmail(String email);
}
