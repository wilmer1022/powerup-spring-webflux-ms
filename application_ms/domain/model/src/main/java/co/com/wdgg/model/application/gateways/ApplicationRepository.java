package co.com.wdgg.model.application.gateways;

import co.com.wdgg.model.application.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {

    Mono<Application> getApplicationById(String id);

    Flux<Application> getApplicationByUserDocumentNumber(String userDocumentNumber);

    Mono<Application> createApplication(Application application);
}
