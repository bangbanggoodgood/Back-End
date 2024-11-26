package home.bangbanggoodgood.controller;

import home.bangbanggoodgood.config.JwtTokenProvider;
import home.bangbanggoodgood.dto.AdminMemberInfoRequestDto;
import home.bangbanggoodgood.dto.AdminMemberInfoResponseDto;
import home.bangbanggoodgood.dto.MemberInfoResponseDto;
import home.bangbanggoodgood.service.MemberService;
import home.bangbanggoodgood.service.PriceCategoryService;
import home.bangbanggoodgood.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StatisticsService statisticsService;
    private final PriceCategoryService priceCategoryService;
    private final MemberService memberService;

    @GetMapping("/statics")
    public ResponseEntity<?> getStatics(@RequestParam String kind) {
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

    @PostMapping("/userList")
    public ResponseEntity<AdminMemberInfoResponseDto> getMemberInfo(@RequestBody AdminMemberInfoRequestDto requestDto) {
        AdminMemberInfoResponseDto responseDto = memberService.getUserInfoByUseId(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
