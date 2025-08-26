package co.com.wdgg.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.wdgg.r2dbc.entities.ApplicationEntity;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Repository interface for managing ApplicationEntity objects.
 * This repository extends ReactiveCrudRepository for non-blocking database operations
 * and provides additional custom methods for finding users.
 *
 * @author wilmer1022
 * @version 0.0.1
 * @since 2023-08-25
 */
public interface ApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, UUID> {

    /**
     * Finds applications by their user document number.
     * This method returns a Flux, which represents a stream of results
     * in a reactive, non-blocking way.
     *
     * @param userDocumentNumber The unique document number of the user.
     * @return A {@link Flux} containing the found {@link ApplicationEntity} or an empty Flux if not found.
     */
    Flux<ApplicationEntity> findByUserDocumentNumber(String userDocumentNumber);
}
