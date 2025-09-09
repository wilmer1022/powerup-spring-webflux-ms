package co.com.wdgg.r2dbc;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.wdgg.r2dbc.entities.ApplicationEntity;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing ApplicationEntity objects.
 * This repository extends ReactiveCrudRepository for non-blocking database
 * operations
 * and provides additional custom methods for finding users.
 *
 * @author wilmer1022
 * @version 0.0.1
 * @since 2023-08-25
 */
public interface ApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, UUID> {

    /**
     * Finds applications by their user email.
     * This method returns a Flux, which represents a stream of results
     * in a reactive, non-blocking way.
     *
     * @param userEmail The unique email of the user.
     * @return A {@link Flux} containing the found {@link ApplicationEntity} or an
     *         empty Flux if not found.
     */
    Flux<ApplicationEntity> findByUserEmail(String userEmail);

    @Query("SELECT " +
            "*" +
            "FROM applications a " +
            "JOIN applications_status ast ON a.application_status_id = ast.id " +
            "JOIN applications_credit_type act ON a.application_credit_type_id = act.id " +
            "WHERE a.application_status_id IN (:statusIds) " +
            "ORDER BY a.credit_period DESC " +
            "OFFSET :offset " +
            "LIMIT :limit")
    Flux<ApplicationEntity> findReviewableApplications(List<UUID> statusIds, int limit, long offset);
}
