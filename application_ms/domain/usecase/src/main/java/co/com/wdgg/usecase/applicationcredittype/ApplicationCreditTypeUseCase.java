package co.com.wdgg.usecase.applicationcredittype;

import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationcredittype.gateways.ApplicationCreditTypeRepository;
import reactor.core.publisher.Mono;

public class ApplicationCreditTypeUseCase {

    private final ApplicationCreditTypeRepository applicationCreditTypeRepository;

    public ApplicationCreditTypeUseCase(ApplicationCreditTypeRepository applicationCreditTypeRepository) {
        this.applicationCreditTypeRepository = applicationCreditTypeRepository;
    }

    public Mono<ApplicationCreditType> getApplicationCreditTypeById(String id) {
        return applicationCreditTypeRepository.getApplicationCreditTypeById(id);
    }
}
