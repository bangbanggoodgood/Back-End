package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.config.JwtTokenProvider;
import home.bangbanggoodgood.dto.AptFinalResponseDto;
import home.bangbanggoodgood.dto.AptRequestDto;
import home.bangbanggoodgood.dto.AptResponseDto;
import home.bangbanggoodgood.service.AptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/deals")
public class AptController {

    private final AptService aptService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping()
    public ResponseEntity<AptFinalResponseDto> getDealList(@RequestHeader("Authorization") String authorizationHeader,
                                                           @RequestParam String presentPage,
                                                            @RequestParam String limit,
                                                            @RequestBody AptRequestDto requestDto) {
        // 토큰 추출
        String token = authorizationHeader.replace("Bearer ", "");

        // 사용자 아이디 추출
        Long memberId = jwtTokenProvider.parseMemberId(token);
        AptFinalResponseDto result = aptService.show(requestDto, memberId, presentPage, limit);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
