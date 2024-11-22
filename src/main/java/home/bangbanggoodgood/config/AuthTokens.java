package home.bangbanggoodgood.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokens {
    private String accessToken; // accessToken 정보 담음.

    public static AuthTokens of(String accessToken) {
        return new AuthTokens(accessToken);
    }
}
