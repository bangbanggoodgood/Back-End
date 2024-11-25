package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService service;

    @GetMapping("/sido")

    public ResponseEntity<List<String>> getSido() {
        List<String> result = service.findSido();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/gugun")
    public ResponseEntity<List<String>> getGugun(@RequestParam String sido) {
        List<String> result = service.findGugun(sido);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/dong")
    public ResponseEntity<List<String>> getDong(@RequestParam String sido, @RequestParam String gugun) {
        List<String> result = service.findDong(sido, gugun);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
