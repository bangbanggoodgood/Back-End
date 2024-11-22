package home.bangbanggoodgood.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor

//토큰을 생성하는 코드
public class AuthTokensGenerator {
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 2; // 2일

    private final JwtTokenProvider jwtTokenProvider;

    // id로 Access Token 생성
    public AuthTokens generate(String memberId) {
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = jwtTokenProvider.accessTokenGenerate(memberId, accessTokenExpiredAt);

        return AuthTokens.of(accessToken);
    }
}
