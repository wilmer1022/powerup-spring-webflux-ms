package co.com.wdgg.api.dto;

import org.springframework.stereotype.Component;

import co.com.wdgg.model.user.User;

@Component
public class UserMapper {
    public User toUser(UserRequest userRequest) {
        return new User(
                null,
                userRequest.documentNumber(),
                userRequest.firstName(),
                userRequest.lastName(),
                userRequest.birthDate(),
                userRequest.address(),
                userRequest.phoneNumber(),
                userRequest.email(),
                userRequest.password(),
                userRequest.salary(),
                null);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.documentNumber(),
                user.firstName(),
                user.lastName(),
                user.email(),
                user.phoneNumber(),
                user.address(),
                user.birthDate(),
                user.salary());
    }
}
