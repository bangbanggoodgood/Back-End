package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/deals")
@RequiredArgsConstructor
public class DetailController {
    private final DealService dealService;

    @GetMapping("/detailGraph")
    public ResponseEntity<Map<String, Double>> getDealGraphAmount(@RequestParam String period,
                                                                   @RequestParam String aptSeq) {
        Map<String, Double> result = dealService.getGraphDetail(aptSeq);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
