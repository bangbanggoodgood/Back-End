package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.dto.AptRequestDto;
import home.bangbanggoodgood.dto.AptResponseDto;
import home.bangbanggoodgood.service.AptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/deals")
public class AptController {

    private final AptService aptService;

    @PostMapping
    public ResponseEntity<List<AptResponseDto>> getDealList(@RequestParam String presentPage,
                                                            @RequestParam String limit,
                                                            @RequestBody AptRequestDto requestDto) {
        List<AptResponseDto> result = aptService.findDealList(requestDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
