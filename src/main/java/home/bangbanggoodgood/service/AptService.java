package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.DongCode;
import home.bangbanggoodgood.dto.AptFinalResponseDto;
import home.bangbanggoodgood.dto.AptRequestDto;
import home.bangbanggoodgood.dto.AptResponseDto;
import home.bangbanggoodgood.repository.AptRepository;
import home.bangbanggoodgood.repository.InfoRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AptService {
    private final AptRepository aptRepository;
    private final InfoService infoService;

    public AptFinalResponseDto show(AptRequestDto dto) {
        List<String> dongCodes;
        String dongCode;
        int targetMinPrice = 0;
        int targetMaxPrice = Integer.MAX_VALUE;
        List<AptResponseDto> result = null;
        if(dto.getTargetMinPrice() != -1) {
            targetMinPrice = dto.getTargetMinPrice();
        }
        if(dto.getTargetMaxPrice() != -1) {
            targetMaxPrice = dto.getTargetMaxPrice();
        }
        if(dto.getDongName().equals("전체")) { // 해당하는 시도, 구군에 해당하는 전체 동
            dongCodes = infoService.findDongCodes(dto.getSidoName(), dto.getGugunName());
            result = findDealListWithDongCodes(dto, dongCodes, targetMinPrice, targetMaxPrice);
        } else {
            dongCode = infoService.findDongCode(dto.getSidoName(), dto.getGugunName(), dto.getDongName());
            result = findDealListWithOneDongCode(dto, dongCode, targetMinPrice, targetMaxPrice);
        }
        int total = result.size();
        return new AptFinalResponseDto(total, result);
    }

    private List<AptResponseDto> findDealListWithOneDongCode(AptRequestDto dto, String dongCode, int targetMinPrice, int targetMaxPrice) {
        List<Tuple> tuples = null;
        if(dto.getAptName() != null) { // 아파트 이름이 입력 됐다면
            tuples = aptRepository.findByDongAndAptName(dongCode, dto.getAptName(), targetMinPrice, targetMaxPrice);
        } else {
            tuples = aptRepository.findByDong(dongCode, targetMinPrice, targetMaxPrice);
        }
        List<AptResponseDto> result = getResult(tuples);
        return result;
    }

    private List<AptResponseDto> findDealListWithDongCodes(AptRequestDto dto, List<String> dongCodes, int targetMinPrice, int targetMaxPrice) {
        List<Tuple> tuples = null;
        if(dto.getAptName() != null) {
            tuples = aptRepository.findBySidoAndGugunAndAptName(dongCodes, dto.getAptName(), targetMinPrice, targetMaxPrice);
        } else {
            tuples = aptRepository.findBySidoAndGugun(dongCodes, targetMinPrice, targetMaxPrice);
        }
        List<AptResponseDto> result = getResult(tuples);
        return result;
    }

    private List<AptResponseDto> getResult(List<Tuple> tuples) {
        List<AptResponseDto> result = new ArrayList<>();
        for(Tuple tuple : tuples) {
            result.add(new AptResponseDto(
                    tuple.get(0, String.class),
                    tuple.get(1, String.class),
                    tuple.get(2, Integer.class),
                    tuple.get(3, BigDecimal.class),
                    tuple.get(4, BigDecimal.class),
                    tuple.get(5, String.class),
                    tuple.get(6, Integer.class),
                    tuple.get(7, Integer.class)
            ));
        }
        return result;
    }

}
