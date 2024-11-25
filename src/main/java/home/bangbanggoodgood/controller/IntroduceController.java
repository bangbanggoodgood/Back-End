package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.dto.IntroduceResponseDto;
import home.bangbanggoodgood.service.IntroduceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class IntroduceController {

    private final IntroduceService introduceService;

    @GetMapping
    public ResponseEntity<IntroduceResponseDto> getComments(@RequestParam String aptSeq) {
        IntroduceResponseDto comment = introduceService.checkDB(aptSeq);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

}
