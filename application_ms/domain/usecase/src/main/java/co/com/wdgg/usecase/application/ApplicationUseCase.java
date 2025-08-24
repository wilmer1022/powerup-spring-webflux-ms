package co.com.wdgg.usecase.application;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.application.gateways.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;

    public Mono<Application> getApplicationById(String id) {
        return applicationRepository.getApplicationById(id);
    }

    public Flux<Application> getApplicationByUserDocumentNumber(String userDocumentNumber) {
        return applicationRepository.getApplicationByUserDocumentNumber(userDocumentNumber);
    }

    public Mono<Application> createApplication(Application application) {
        return applicationRepository.createApplication(application);
    }
}
