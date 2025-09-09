package co.com.wdgg.r2dbc;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.application.gateways.ApplicationRepository;
import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationcredittype.gateways.ApplicationCreditTypeRepository;
import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import co.com.wdgg.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.wdgg.r2dbc.entities.ApplicationEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ApplicationReactiveRepositoryAdapter implements ApplicationRepository {

    private final ApplicationReactiveRepository applicationReactiveRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final ApplicationCreditTypeRepository applicationCreditTypeRepository;

    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository applicationReactiveRepository,
            ApplicationStatusRepository applicationStatusRepository,
            ApplicationCreditTypeRepository applicationCreditTypeRepository) {
        this.applicationStatusRepository = applicationStatusRepository;
        this.applicationCreditTypeRepository = applicationCreditTypeRepository;
        this.applicationReactiveRepository = applicationReactiveRepository;
    }

    @Override
    public Mono<Application> getApplicationById(String id) {
        return applicationReactiveRepository.findById(UUID.fromString(id))
                .flatMap(this::loadApplicationStatusAndCreditTypeAndToDomain);
    }

    @Override
    public Flux<Application> getApplicationByUserEmail(String userEmail) {
        return applicationReactiveRepository.findByUserEmail(userEmail)
                .flatMap(this::loadApplicationStatusAndCreditTypeAndToDomain);
    }

    @Override
    public Mono<Application> createApplication(Application application) {
        return applicationReactiveRepository.save(toEntity(application))
                .flatMap(this::loadApplicationStatusAndCreditTypeAndToDomain);
    }

    @Override
    public Flux<Application> getReviewableApplications(List<UUID> statusIds, int limit, long offset) {
        return applicationReactiveRepository.findReviewableApplications(statusIds, limit, offset)
                .flatMap(this::loadApplicationStatusAndCreditTypeAndToDomain);
    }

    private ApplicationEntity toEntity(Application application) {
        return new ApplicationEntity(
                application.id(),
                application.userEmail(),
                application.amount(),
                application.creditPeriod(),
                application.applicationStatus() != null ? application.applicationStatus().id() : null,
                application.applicationCreditType() != null ? application.applicationCreditType().id() : null,
                null,
                null);
    }

    private Application toDomain(ApplicationEntity applicationEntity, ApplicationStatus applicationStatus,
            ApplicationCreditType applicationCreditType) {
        return new Application(
                applicationEntity.getId(),
                applicationEntity.getUserEmail(),
                applicationEntity.getAmount(),
                applicationEntity.getCreditPeriod(),
                applicationStatus,
                applicationCreditType);
    }

    private Mono<Application> loadApplicationStatusAndCreditTypeAndToDomain(ApplicationEntity applicationEntity) {
        Mono<ApplicationStatus> applicationStatusMono = applicationStatusRepository
                .getApplicationStatusById(applicationEntity.getApplicationStatusId().toString());
        Mono<ApplicationCreditType> applicationCreditTypeMono = applicationCreditTypeRepository
                .getApplicationCreditTypeById(applicationEntity.getApplicationCreditTypeId().toString());

        return Mono.zip(applicationStatusMono, applicationCreditTypeMono)
                .flatMap(tuple -> {
                    ApplicationStatus status = tuple.getT1();
                    ApplicationCreditType creditType = tuple.getT2();
                    return Mono.just(toDomain(applicationEntity, status, creditType));
                });
    }
}
