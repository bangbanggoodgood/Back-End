package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.*;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository repository;
    private final MemberRepository memberRepository;
    private final AptRepository aptRepository;
    private final InfoRepository infoRepository;
    private final LikeRepository likeRepository;

    public LikeResponseDto postLike(Long memberId, LikeRequestDto requestDto) {
        Members members = memberRepository.findMemberById(memberId);
        AptInfos infos = aptRepository.findByAptSeq(requestDto.getAptSeq());
        Optional<Likes> likes = repository.findByMemberAndAptInfo_AptSeq(members, requestDto.getAptSeq());
        if(likes.isEmpty()) {
            repository.save(LikeDto.toEntity(members, infos));
            infos.updateCount(1l);
        } else {
            repository.deleteById(likes.get().getId());
            infos.updateCount(0l);
        }

        return new LikeResponseDto(infos.getCount());
    }

    public AptFinalResponseDto getLikes(Long memberId) {
        List<Tuple> tuples = findSidoAndGugun(memberId);
        List<AptResponseDto> result = getResult(tuples, memberId);
        int total = result.size();
        return new AptFinalResponseDto(total, result);
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

    private List<Tuple> findSidoAndGugun(Long memberId) {
        List<String> dongCode = repository.findDongCodeByAptInfos(memberId);
        List<Tuple> tuples = new ArrayList<>();
        for(String dong : dongCode) {
            String sidoName = infoRepository.findSidoNameByDongCode(dong);
            String gugunName = infoRepository.findGugunNameByDongCodeAndSidoName(sidoName, dong);
            String sidoGugun = sidoName +" "+ gugunName;

            System.out.println("dong : " + dong + " " + "sidoName : " + sidoName + "gugunName : " + gugunName);
            tuples = repository.findAptInfosByMemberId(memberId, sidoGugun);
        }

        return tuples;
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
