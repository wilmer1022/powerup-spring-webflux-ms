package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.wdgg.r2dbc.entities.ApplicationCreditTypeEntity;

public interface ApplicationCreditTypeReactiveRepository extends ReactiveCrudRepository<ApplicationCreditTypeEntity, UUID> {
    
}
