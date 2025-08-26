package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationcredittype.gateways.ApplicationCreditTypeRepository;
import co.com.wdgg.r2dbc.entities.ApplicationCreditTypeEntity;
import reactor.core.publisher.Mono;

@Repository
public class ApplicationCreditTypeReactiveRepositoryAdapter implements ApplicationCreditTypeRepository {

    private final ApplicationCreditTypeReactiveRepository applicationCreditTypeReactiveRepository;

    public ApplicationCreditTypeReactiveRepositoryAdapter(ApplicationCreditTypeReactiveRepository applicationCreditTypeReactiveRepository) {
        this.applicationCreditTypeReactiveRepository = applicationCreditTypeReactiveRepository;
    }
    
    public Mono<ApplicationCreditType> getApplicationCreditTypeById(String id) {
        return applicationCreditTypeReactiveRepository.findById(UUID.fromString(id))
                .map(this::toDomain);
    }

    private ApplicationCreditType toDomain(ApplicationCreditTypeEntity applicationCreditTypeEntity) {
        return new ApplicationCreditType(
                applicationCreditTypeEntity.getId(),
                applicationCreditTypeEntity.getCreditType(),
                applicationCreditTypeEntity.getMinAmount(),
                applicationCreditTypeEntity.getMaxAmount(),
                applicationCreditTypeEntity.getInterestRate(),
                applicationCreditTypeEntity.isAutomaticReview());
    }
}
