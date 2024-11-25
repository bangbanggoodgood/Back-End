package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.config.JwtTokenProvider;
import home.bangbanggoodgood.dto.TimeRequestDto;
import home.bangbanggoodgood.service.TimeDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/time")
public class TimeController {
    private final TimeDataService timeDataService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<?> setStayTime(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody TimeRequestDto timeRequestDto) {

        // 토큰 추출
        String token = authorizationHeader.replace("Bearer ", "");

        // 사용자 아이디 추출
        Long memberId = jwtTokenProvider.parseMemberId(token);
        timeDataService.saveTimeData(timeRequestDto, memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
