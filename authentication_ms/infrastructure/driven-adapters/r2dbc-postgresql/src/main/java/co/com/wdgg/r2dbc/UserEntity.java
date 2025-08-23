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
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("document_number")
    private String documentNumber;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("address")
    private String address;

    @Column("phone_number")
    private String phoneNumber;

    @Column("email")
    private String email;

    @Column("salary")
    private BigDecimal salary;
}
