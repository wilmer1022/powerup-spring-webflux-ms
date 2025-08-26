package co.com.wdgg.model.applicationstatus.gateways;

import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import reactor.core.publisher.Mono;

public interface ApplicationStatusRepository {

    Mono<ApplicationStatus> getApplicationStatusById(String id);

    Mono<ApplicationStatus> getApplicationStatusByStatus(String status);
}
