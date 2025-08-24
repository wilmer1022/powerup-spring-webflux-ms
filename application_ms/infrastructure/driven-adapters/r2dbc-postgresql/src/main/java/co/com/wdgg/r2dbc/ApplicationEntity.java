package co.com.wdgg.r2dbc;

import org.springframework.data.annotation.Id;
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

    @Column("credit_type")
    private String creditType;

    @Column("credit_status")
    private String creditStatus;

    public ApplicationEntity(String userDocumentNumber, BigDecimal amount, LocalDate creditPeriod,
            String creditType) {
        this.userDocumentNumber = userDocumentNumber;
        this.amount = amount;
        this.creditPeriod = creditPeriod;
        this.creditType = creditType;
    }
}
