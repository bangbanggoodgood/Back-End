package home.bangbanggoodgood.controller;

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

    @PostMapping("/{memberId}")
    public ResponseEntity<AptFinalResponseDto> getDealList(@RequestParam String presentPage,
                                                            @RequestParam String limit,
                                                            @PathVariable Long memberId,
                                                            @RequestBody AptRequestDto requestDto) {
        AptFinalResponseDto result = aptService.show(requestDto, memberId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
