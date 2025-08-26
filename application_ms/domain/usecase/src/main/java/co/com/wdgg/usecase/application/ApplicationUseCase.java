package co.com.wdgg.usecase.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.application.gateways.ApplicationRepository;
import co.com.wdgg.model.application.validators.ApplicationValidator;
import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import co.com.wdgg.usecase.applicationcredittype.ApplicationCreditTypeUseCase;
import co.com.wdgg.usecase.applicationstatus.ApplicationStatusUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final ApplicationCreditTypeUseCase applicationCreditTypeUseCase;
    private final ApplicationStatusUseCase applicationStatusUseCase;

    public Mono<Application> getApplicationById(String id) {
        return applicationRepository.getApplicationById(id);
    }

    public Flux<Application> getApplicationByUserDocumentNumber(String userDocumentNumber) {
        return applicationRepository.getApplicationByUserDocumentNumber(userDocumentNumber);
    }

    public Mono<Application> createApplication(Application application) {
        return Mono.just(application)
            .flatMap(app -> {
                final ApplicationValidator applicationValidator = new ApplicationValidator(app);
                return Mono.justOrEmpty(applicationValidator.validateUserDocumentNumber())
                    .mergeWith(Mono.justOrEmpty(applicationValidator.validateAmount()))
                    .mergeWith(Mono.justOrEmpty(applicationValidator.validateCreditType()))
                    .mergeWith(Mono.justOrEmpty(applicationValidator.validateAmountForm()))
                    .collectList()
                    .flatMap(errors -> {
                        if (!errors.isEmpty()) {
                            String errorMessage = String.join(", ", errors);
                            return Mono.error(new IllegalArgumentException("Errores en el formulario de registro: " + errorMessage));
                        }
                        return Mono.just(app);
                    });
            })
            .flatMap(app ->
                Mono.zip(
                    applicationCreditTypeUseCase.getApplicationCreditTypeById(app.applicationCreditType().id().toString()),
                    applicationStatusUseCase.getApplicationStatusByStatus("PENDIENTE")
                )
                .flatMap(tuple -> {
                    ApplicationCreditType creditType = tuple.getT1();
                    ApplicationStatus status = tuple.getT2();

                    Application appWithCreditTypeAndStatus = new Application(
                        app.id(),
                        app.userDocumentNumber(),
                        app.amount(),
                        app.creditPeriod(),
                        status,
                        creditType
                    );
                    
                    return applicationRepository.createApplication(appWithCreditTypeAndStatus);
                })
            );
    }
}
