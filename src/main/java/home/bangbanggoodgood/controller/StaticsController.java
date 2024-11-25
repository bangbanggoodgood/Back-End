package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.repository.StatisticsRepository;
import home.bangbanggoodgood.service.PriceCategoryService;
import home.bangbanggoodgood.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/admin/statics")
@RequiredArgsConstructor
public class StaticsController {

    private final StatisticsService statisticsService;
    private final PriceCategoryService priceCategoryService;

    @GetMapping()
    public ResponseEntity<?> getStatics(@RequestParam String kind) {
        System.out.println("controller 들어와?");
        Map<String, Object> result = null;
        if(kind.equalsIgnoreCase("sex")) {
            result = statisticsService.getCategoryStatistics("sex");
        } else if(kind.equalsIgnoreCase("age")) {
            result = statisticsService.getCategoryStatistics("age");
        } else if(kind.equalsIgnoreCase("price")) {
            result = priceCategoryService.categorizeMembersByPrice();
        } else if(kind.equalsIgnoreCase("infra")) {

        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
