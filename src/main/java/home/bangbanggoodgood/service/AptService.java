package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.DongCode;
import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.AptFinalResponseDto;
import home.bangbanggoodgood.dto.AptRequestDto;
import home.bangbanggoodgood.dto.AptResponseDto;
import home.bangbanggoodgood.repository.AptRepository;
import home.bangbanggoodgood.repository.InfoRepository;
import home.bangbanggoodgood.repository.LikeRepository;
import home.bangbanggoodgood.repository.MemberRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AptService {
    private final AptRepository aptRepository;
    private final InfoService infoService;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;

    public AptFinalResponseDto show(AptRequestDto dto, Long memberId) {
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
            result = findDealListWithDongCodes(dto, dongCodes, targetMinPrice, targetMaxPrice, memberId);
        } else {
            dongCode = infoService.findDongCode(dto.getSidoName(), dto.getGugunName(), dto.getDongName());
            result = findDealListWithOneDongCode(dto, dongCode, targetMinPrice, targetMaxPrice, memberId);
        }
        int total = result.size();
        return new AptFinalResponseDto(total, result);
    }

    private List<AptResponseDto> findDealListWithOneDongCode(AptRequestDto dto, String dongCode, int targetMinPrice, int targetMaxPrice, Long memberId) {
        List<Tuple> tuples = null;
        if(dto.getAptName() != null) { // 아파트 이름이 입력 됐다면
            tuples = aptRepository.findByDongAndAptName(dto.getSidoName(), dto.getGugunName() ,dongCode, dto.getAptName(), targetMinPrice, targetMaxPrice);
        } else {
            tuples = aptRepository.findByDong(dto.getSidoName(), dto.getGugunName(), dongCode, targetMinPrice, targetMaxPrice);
        }
        List<AptResponseDto> result = getResult(tuples, memberId);
        return result;
    }

    private List<AptResponseDto> findDealListWithDongCodes(AptRequestDto dto, List<String> dongCodes, int targetMinPrice, int targetMaxPrice, Long memberId) {
        List<Tuple> tuples = null;
        if(dto.getAptName() != null) {
            tuples = aptRepository.findBySidoAndGugunAndAptName(dto.getSidoName(), dto.getGugunName(), dongCodes, dto.getAptName(), targetMinPrice, targetMaxPrice);
        } else {
            tuples = aptRepository.findBySidoAndGugun(dto.getSidoName(), dto.getGugunName(), dongCodes, targetMinPrice, targetMaxPrice);
        }
        List<AptResponseDto> result = getResult(tuples, memberId);
        return result;
    }

    private List<AptResponseDto> getResult(List<Tuple> tuples, Long memberId) {
        List<AptResponseDto> result = new ArrayList<>();
        for(Tuple tuple : tuples) {
            Long like = isLikeNow(tuple.get(0, String.class), memberId);
            result.add(new AptResponseDto(
                    tuple.get(0, String.class),
                    tuple.get(1, String.class),
                    tuple.get(2, Integer.class),
                    tuple.get(3, BigDecimal.class),
                    tuple.get(4, BigDecimal.class),
                    tuple.get(5, String.class),
                    tuple.get(6, Integer.class),
                    tuple.get(7, Integer.class),
                    like
            ));
        }
        return result;
    }

    private Long isLikeNow(String aptSeq, Long memberId) {
        Members members = memberRepository.findMemberById(memberId);
        Optional<Likes> likes = likeRepository.findByMemberAndAptInfo_AptSeq(members, aptSeq);
        if(likes.isEmpty()) {
            return 0L;
        }
        return 1L;
    }

}