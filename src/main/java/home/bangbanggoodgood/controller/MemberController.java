package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.config.JwtTokenProvider;
import home.bangbanggoodgood.dto.*;
import home.bangbanggoodgood.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원 가입
    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestBody MemberSignUpRequestDto requestDto) {
        // 토큰 추출
        String token = authorizationHeader.replace("Bearer ", "");

        // 사용자 아이디 추출
        Long memberId = jwtTokenProvider.parseMemberId(token);
        SignUpResponseDto responseDto = memberService.signUp(requestDto, memberId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 회원 정보 조회
    @GetMapping
    public ResponseEntity<MemberInfoResponseDto> getMemberInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.parseMemberId(token);
        MemberInfoResponseDto responseDto = memberService.getUserInfo(memberId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMember(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.parseMemberId(token);

        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/check")
    public ResponseEntity<CheckResponseDto> checkValid(@RequestBody CheckRequestDto checkRequestDto) {
        CheckResponseDto responseDto = memberService.checkVaildId(checkRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
