package home.bangbanggoodgood.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.bangbanggoodgood.config.AuthTokens;
import home.bangbanggoodgood.config.AuthTokensGenerator;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.LoginResponseDto;
import home.bangbanggoodgood.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class KakaoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;

    @Value("${kakao.key.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectURI;

    public LoginResponseDto kakaoLogin(String code) {

        // 인가 코드로 액세스 토큰 요청
        String accessToken = getAccessToken(code);

        // 토큰으로 카카오 API 호출
        HashMap<String, Object> userInfo = getKakaoUserInfo(accessToken);

        // 카카오 ID로 회원가입 & 로직 처리
        LoginResponseDto kakaoLoginResponseDto = kakaoUserLogin(userInfo);

        return kakaoLoginResponseDto;

    }

    private LoginResponseDto kakaoUserLogin(HashMap<String, Object> userInfo) {
        Long socialId = Long.valueOf(userInfo.get("socialId").toString());

        Members member = memberRepository.findBySocialId(socialId);
        if(member == null) {
            member = new Members();
            member.setSocialId(socialId);
            member.setIsSurvey(false);
            memberRepository.save(member);
        }
        AuthTokens token = authTokensGenerator.generate(socialId.toString());

        LoginResponseDto responseDto = new LoginResponseDto(socialId, token.getAccessToken());
        return responseDto;
    }

    private HashMap<String, Object> getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<String, Object>();

        //Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼낸다
        String responseBody = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try{
            jsonNode = mapper.readTree(responseBody);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Long id = jsonNode.get("id").asLong();
        userInfo.put("socialId", id);

        return userInfo;
    }

    private String getAccessToken(String code) {
        // HttpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Http Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectURI);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = mapper.readTree(responseBody);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode.get("access_token").asText();
    }
}
