package co.com.wdgg.r2dbc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table("applications")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("user_document_number")
    private String userDocumentNumber;

    @Column("amount")
    private BigDecimal amount;

    @Column("credit_period")
    private LocalDate creditPeriod; 

    @Column("application_status_id")
    private UUID applicationStatusId;

    @Column("application_credit_type_id")
    private UUID applicationCreditTypeId;

    @Transient
    private ApplicationStatusEntity applicationStatus;

    @Transient
    private ApplicationCreditTypeEntity applicationCreditType;
}
