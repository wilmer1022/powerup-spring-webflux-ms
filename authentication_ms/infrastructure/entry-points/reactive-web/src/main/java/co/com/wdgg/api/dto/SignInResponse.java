package co.com.wdgg.api.dto;

public record SignInResponse(
        UserResponse user,
        String token) {

}
