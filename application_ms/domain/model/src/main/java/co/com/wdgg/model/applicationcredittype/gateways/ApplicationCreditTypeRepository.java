package co.com.wdgg.model.applicationcredittype.gateways;

import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import reactor.core.publisher.Mono;

public interface ApplicationCreditTypeRepository {

    Mono<ApplicationCreditType> getApplicationCreditTypeById(String id);
}
