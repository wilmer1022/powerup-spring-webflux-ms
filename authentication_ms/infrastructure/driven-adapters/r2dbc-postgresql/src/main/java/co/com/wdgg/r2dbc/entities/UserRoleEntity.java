package co.com.wdgg.r2dbc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table("user_roles")
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity {
    
    @Id
    @Column("id")
    private UUID id;

    @Column("role")
    private String role;

    @Column("description")
    private String description;
}
