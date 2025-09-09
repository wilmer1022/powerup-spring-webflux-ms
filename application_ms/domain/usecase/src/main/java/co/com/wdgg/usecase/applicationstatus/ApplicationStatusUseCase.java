package co.com.wdgg.usecase.applicationstatus;

import java.util.List;

import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import co.com.wdgg.model.applicationstatus.gateways.ApplicationStatusRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ApplicationStatusUseCase {

    private final ApplicationStatusRepository applicationStatusRepository;

    public ApplicationStatusUseCase(ApplicationStatusRepository applicationStatusRepository) {
        this.applicationStatusRepository = applicationStatusRepository;
    }

    public Mono<ApplicationStatus> getApplicationStatusById(String id) {
        return applicationStatusRepository.getApplicationStatusById(id);
    }

    public Mono<ApplicationStatus> getApplicationStatusByStatus(String status) {
        return applicationStatusRepository.getApplicationStatusByStatus(status);
    }

    public Flux<ApplicationStatus> getApplicationStatusesByStatuses(List<String>  statuses) {
        return applicationStatusRepository.getApplicationStatusesByStatuses(statuses);
    }
}
