package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import co.com.wdgg.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.wdgg.r2dbc.entities.ApplicationStatusEntity;
import reactor.core.publisher.Mono;

@Repository
public class ApplicationStatusReactiveRepositoryAdapter implements ApplicationStatusRepository {
    
    private final ApplicationStatusReactiveRepository applicationStatusReactiveRepository;

    public ApplicationStatusReactiveRepositoryAdapter(ApplicationStatusReactiveRepository applicationStatusReactiveRepository) {
        this.applicationStatusReactiveRepository = applicationStatusReactiveRepository;
    }

    @Override
    public Mono<ApplicationStatus> getApplicationStatusById(String id) {
        return applicationStatusReactiveRepository.findById(UUID.fromString(id))
                .map(this::toDomain);
    }

    @Override
    public Mono<ApplicationStatus> getApplicationStatusByStatus(String status) {
        return applicationStatusReactiveRepository.findByStatus(status)
                .map(this::toDomain);
    }

    private ApplicationStatus toDomain(ApplicationStatusEntity applicationStatusEntity) {
        return new ApplicationStatus(
                applicationStatusEntity.getId(),
                applicationStatusEntity.getStatus(),
                applicationStatusEntity.getDescription());
    }
}
