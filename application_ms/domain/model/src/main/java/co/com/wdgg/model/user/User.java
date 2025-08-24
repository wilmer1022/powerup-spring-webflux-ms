package co.com.wdgg.model.user;

import java.util.UUID;

public record User(UUID id,String documentNumber, String firstName, String lastName, String email, String salary) {
}
