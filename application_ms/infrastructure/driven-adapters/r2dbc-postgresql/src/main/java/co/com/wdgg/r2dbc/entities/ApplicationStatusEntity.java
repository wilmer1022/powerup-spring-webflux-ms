package co.com.wdgg.r2dbc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table("applications_status")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusEntity {
    
    @Id
    @Column("id")
    private UUID id;

    @Column("status")
    private String status;

    @Column("description")
    private String description;
}
