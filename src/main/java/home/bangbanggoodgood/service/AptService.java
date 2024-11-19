package home.bangbanggoodgood.service;

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

    public List<AptResponseDto> findDealList(AptRequestDto dto) {
        System.out.println("sido : " + dto.getSidoName() + " " + "gugun : " + dto.getGugunName() + " " + "dong : " + dto.getDongName());
        String dongCode = infoService.findDongCode(dto.getSidoName(), dto.getGugunName(), dto.getDongName());
        System.out.println("dongcode : " + dongCode);
        List<Tuple> tuples = null;
        if(dto.getAptName() != null) { // 아파트 이름이 입력 됐다면
            tuples = aptRepository.findByDongAndAptName(dongCode, dto.getAptName());
        } else {
            tuples = aptRepository.findByDong(dongCode);
            System.out.println("튜플 사이즈 : " + tuples.size());
        }

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
        System.out.println("result 사이즈" + result.size());
        return result;
    }
}
