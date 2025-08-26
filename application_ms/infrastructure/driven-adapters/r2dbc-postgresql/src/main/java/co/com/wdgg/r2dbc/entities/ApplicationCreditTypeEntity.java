package co.com.wdgg.r2dbc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table("applications_credit_type")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreditTypeEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("credit_type")
    private String creditType;

    @Column("min_amount")
    private BigDecimal minAmount;

    @Column("max_amount")
    private BigDecimal maxAmount;

    @Column("interest_rate")
    private BigDecimal interestRate;

    @Column("automatic_review")
    private boolean automaticReview;
}
