package co.com.wdgg.usecase.application;

import java.util.List;
import java.util.UUID;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.application.gateways.ApplicationRepository;
import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import co.com.wdgg.usecase.application.validators.ApplicationValidator;
import co.com.wdgg.usecase.applicationcredittype.ApplicationCreditTypeUseCase;
import co.com.wdgg.usecase.applicationstatus.ApplicationStatusUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final ApplicationCreditTypeUseCase applicationCreditTypeUseCase;
    private final ApplicationStatusUseCase applicationStatusUseCase;

    public ApplicationUseCase(ApplicationRepository applicationRepository,
            ApplicationCreditTypeUseCase applicationCreditTypeUseCase,
            ApplicationStatusUseCase applicationStatusUseCase) {
        this.applicationRepository = applicationRepository;
        this.applicationCreditTypeUseCase = applicationCreditTypeUseCase;
        this.applicationStatusUseCase = applicationStatusUseCase;
    }

    public Mono<Application> getApplicationById(String id) {
        return applicationRepository.getApplicationById(id);
    }

    public Flux<Application> getApplicationByUserEmail(String userDocumentNumber) {
        return applicationRepository.getApplicationByUserEmail(userDocumentNumber);
    }

    public Mono<Application> createApplication(Application application) {
        return Mono.just(application)
                .flatMap(app -> {
                    final ApplicationValidator applicationValidator = new ApplicationValidator(app);
                    return Mono.justOrEmpty(applicationValidator.validateUserEmail())
                            .mergeWith(Mono.justOrEmpty(applicationValidator.validateAmount()))
                            .mergeWith(Mono.justOrEmpty(applicationValidator.validateCreditType()))
                            .mergeWith(Mono.justOrEmpty(applicationValidator.validateAmountForm()))
                            .collectList()
                            .flatMap(errors -> {
                                if (!errors.isEmpty()) {
                                    String errorMessage = String.join(", ", errors);
                                    return Mono.error(new IllegalArgumentException(
                                            "Errores en el formulario de registro: " + errorMessage));
                                }
                                return Mono.just(app);
                            });
                })
                .flatMap(app -> Mono.zip(
                        applicationCreditTypeUseCase
                                .getApplicationCreditTypeById(app.applicationCreditType().id().toString()),
                        applicationStatusUseCase.getApplicationStatusByStatus("PENDIENTE"))
                        .flatMap(tuple -> {
                            ApplicationCreditType creditType = tuple.getT1();
                            ApplicationStatus status = tuple.getT2();

                            Application appWithCreditTypeAndStatus = new Application(
                                    app.id(),
                                    app.userEmail(),
                                    app.amount(),
                                    app.creditPeriod(),
                                    status,
                                    creditType);

                            return applicationRepository.createApplication(appWithCreditTypeAndStatus);
                        }));
    }

    public Flux<Application> getReviewableApplications(int limit, long offset) {
        Mono<List<UUID>> applicationStatusIds = applicationStatusUseCase
                .getApplicationStatusesByStatuses(List.of("PENDIENTE", "REVISION", "RECHAZADO"))
                .map(ApplicationStatus::id).collectList();
        return applicationStatusIds.flatMapMany(statusIds ->
            applicationRepository.getReviewableApplications(statusIds, limit, offset)
        );
    }
}
