package co.com.wdgg.model.userrole;

import java.util.UUID;

public record UserRole(
        UUID id,
        String role,
        String description) {
}
