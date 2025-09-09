package co.com.wdgg.model.applicationstatus.gateways;

import java.util.List;

import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationStatusRepository {

    Mono<ApplicationStatus> getApplicationStatusById(String id);

    Mono<ApplicationStatus> getApplicationStatusByStatus(String status);

    Flux<ApplicationStatus> getApplicationStatusesByStatuses(List<String> statuses);
}
