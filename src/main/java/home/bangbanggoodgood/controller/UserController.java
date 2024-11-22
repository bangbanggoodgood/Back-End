package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.LoginResponseDto;
import home.bangbanggoodgood.repository.MemberRepository;
import home.bangbanggoodgood.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

        Long socialId = Long.valueOf(responseDto.getId());
        Members member = memberRepository.findBySocialId(socialId);

        // 회원가입 상태 및 설문 여부 확인
        if (member == null || !member.getIsSurvey()) {
            // 가입하지 않았거나 설문 미진행인 경우
            response.sendRedirect("http://localhost:5173/signUp");
        } else {
            // 이미 가입하고 설문을 진행한 경우
            response.sendRedirect("http://localhost:5173");
        }
    }

}
