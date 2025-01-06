package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.config.JwtTokenProvider;
import home.bangbanggoodgood.dto.AptFinalResponseDto;
import home.bangbanggoodgood.dto.AptResponseDto;
import home.bangbanggoodgood.dto.LikeRequestDto;
import home.bangbanggoodgood.dto.LikeResponseDto;
import home.bangbanggoodgood.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<LikeResponseDto> postLike(@RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestBody LikeRequestDto requestDto) {

        String token = authorizationHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.parseMemberId(token);
        LikeResponseDto count = likeService.postLike(memberId, requestDto);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<AptFinalResponseDto> getLikes(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestParam int presentPage,
                                                        @RequestParam int limit) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.parseMemberId(token);
        AptFinalResponseDto result = likeService.getLikes(memberId, presentPage, limit);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
