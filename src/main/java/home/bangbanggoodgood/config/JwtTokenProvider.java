package home.bangbanggoodgood.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
// jwt 관련 로직을 처리하는 컴포넌트. 현재는 accessToken과 refresh 토큰 생성 함수만 있다.
public class JwtTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Key key; // Key 타입으로 JWT 서명을 위한 키

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //Base64로 디코딩
        this.key = Keys.hmacShaKeyFor(keyBytes); // 비밀 키 생성
    }

    public String accessTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256) // 위에 생성한 비밀 키
                .compact();
    }
}
