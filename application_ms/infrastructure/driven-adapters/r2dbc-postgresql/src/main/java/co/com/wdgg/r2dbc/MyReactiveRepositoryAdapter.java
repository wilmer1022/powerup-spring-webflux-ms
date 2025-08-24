package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.application.gateways.ApplicationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter implements ApplicationRepository {

    private final MyReactiveRepository repository;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Application> getApplicationById(String id) {
        return repository.findById(UUID.fromString(id))
                .map(this::toDomain);
    }

    @Override
    public Flux<Application> getApplicationByUserDocumentNumber(String userDocumentNumber) {
        return repository.findByUserDocumentNumber(userDocumentNumber)
                .map(this::toDomain);
    }

    @Override
    public Mono<Application> createApplication(Application application) {
        System.out.println("pre transform" + application);
        System.out.println("post transform" + toEntity(application));
        return repository.save(toEntity(application))
                .map(this::toDomain);
    }

    private ApplicationEntity toEntity(Application application) {
        return new ApplicationEntity(
                application.id(),
                application.userDocumentNumber(),
                application.amount(),
                application.creditPeriod(),
                application.creditType(),
                application.creditStatus());
    }

    private Application toDomain(ApplicationEntity applicationEntity) {
        return new Application(
                applicationEntity.getId(),
                applicationEntity.getUserDocumentNumber(),
                applicationEntity.getAmount(),
                applicationEntity.getCreditPeriod(),
                applicationEntity.getCreditType(),
                applicationEntity.getCreditStatus());
    }
}
