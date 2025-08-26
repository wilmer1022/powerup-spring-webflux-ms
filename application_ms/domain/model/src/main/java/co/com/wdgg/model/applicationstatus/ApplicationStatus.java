package co.com.wdgg.model.applicationstatus;

import java.util.UUID;

public record ApplicationStatus(
        UUID id,
        String status,
        String description) {
}
