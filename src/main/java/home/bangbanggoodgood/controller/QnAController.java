package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.dto.QnARequestDto;
import home.bangbanggoodgood.dto.QnAResponseDto;
import home.bangbanggoodgood.service.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QnAController {

    private final QnAService qnAService;

    @PostMapping
    public ResponseEntity<QnAResponseDto> getAnswer(@RequestBody QnARequestDto requestDto) {
        QnAResponseDto answer = qnAService.askToAI(requestDto);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
