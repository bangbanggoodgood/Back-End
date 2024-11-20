package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.dto.LikeRequestDto;
import home.bangbanggoodgood.dto.LikeResponseDto;
import home.bangbanggoodgood.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponseDto> postLike(@RequestBody LikeRequestDto requestDto) {
        LikeResponseDto count = likeService.postLike(requestDto.getMemberId(), requestDto);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
