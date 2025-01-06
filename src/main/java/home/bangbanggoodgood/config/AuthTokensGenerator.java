package home.bangbanggoodgood.config;

import home.bangbanggoodgood.dto.CustomMemberInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor

//토큰을 생성하는 코드
public class AuthTokensGenerator {
    private static final String BEARER_TYPE = "Bearer";
  //  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 2; // 2일
  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 1분

    private final JwtTokenProvider jwtTokenProvider;

    // id로 Access Token 생성
    public AuthTokens generate(CustomMemberInfoDto customMemberInfoDto) {
        long now = (new Date()).getTime();
        String accessToken = jwtTokenProvider.accessTokenGenerate(customMemberInfoDto, ACCESS_TOKEN_EXPIRE_TIME);
        System.out.println("생성 access 토큰 : " + accessToken);
        return AuthTokens.of(accessToken);
    }
}
