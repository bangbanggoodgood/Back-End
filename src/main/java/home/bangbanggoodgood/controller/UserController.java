package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.LoginResponseDto;
import home.bangbanggoodgood.repository.MemberRepository;
import home.bangbanggoodgood.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class UserController {

    private final KakaoService kakaoService;
    private final MemberRepository memberRepository;

    @GetMapping("/kakao")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        LoginResponseDto responseDto = kakaoService.kakaoLogin(code);
        String socialId = responseDto.getId();
        System.out.println("초기 소셜 아이디 받아와 지는지 : " + socialId);
        String accessToken = responseDto.getAccessToken();
        Members member = memberRepository.findBySocialId(socialId);

        // 회원가입 상태 및 설문 여부 확인
        if (member == null || !member.getIsSurvey()) {
            // 가입하지 않았거나 설문 미진행인 경우
            // JWT 토큰을 리디렉션 URL의 쿼리 파라미터로 포함시켜 리디렉션
            String redirectUrl = "http://localhost:5173/signUp?accessToken=" + accessToken;
            response.sendRedirect(redirectUrl);
        } else {
            // 이미 가입하고 설문을 진행한 경우
            String redirectUrl = "http://localhost:5173?accessToken=" + accessToken;
            response.sendRedirect(redirectUrl);
        }

    }
}

