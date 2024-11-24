package home.bangbanggoodgood.config;

import home.bangbanggoodgood.dto.CustomMemberInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
// jwt 관련 로직을 처리하는 컴포넌트. 현재는 accessToken과 refresh 토큰 생성 함수만 있다.
public class JwtTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Key key; // Key 타입으로 JWT 서명을 위한 키

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //Base64로 디코딩
        this.key = Keys.hmacShaKeyFor(keyBytes); // 비밀 키 생성
    }

    public String accessTokenGenerate(CustomMemberInfoDto customMemberInfoDto, Date expiredAt) {
        Claims claims = Jwts.claims();
        claims.put("auth", customMemberInfoDto.getAuthority());
        claims.put("memberId", customMemberInfoDto.getMemberId());
        claims.put("sub", customMemberInfoDto.getSocialId());
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiresAt = now.plusSeconds(expiredAt.getTime() / 1000);
        Date expirationDate = Date.from(expiresAt.toInstant());

        return Jwts.builder()
                .setSubject(customMemberInfoDto.getSocialId()) // socialId
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256) // 위에 생성한 비밀 키
                .compact();
    }


    public Long parseMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        if(claims.get("auth")==null) {
            throw new RuntimeException("INVALID ACCESS TOKEN");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserDetails userDetails = new User(claims.getSubject(),"", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }


    // JWT의 페이로드에서 클레임을 추출하는 코드 . 페이로드 == 클레임
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder() // JWT 파서를 생성
                    .setSigningKey(key) // 서명 검증에 사용할 키를 설정
                    .build()
                    .parseClaimsJws(accessToken) // JWT 파싱하고 유효성 검증
                    .getBody(); // JWT의 페이로드 반환
        } catch(ExpiredJwtException e) {
            return e.getClaims(); // 만료된 토큰의 클레임을 반환. 예외 객체의 클레임 정보가 들어있다.
        }
    }

    // JWT 검증하기
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch(ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }


}
