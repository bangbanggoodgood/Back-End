package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.*;
import home.bangbanggoodgood.repository.AptRepository;
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
        List<Tuple> tuples = null;
        tuples = repository.findAptInfosByMemberId(memberId);
        List<AptResponseDto> result = getResult(tuples);
        int total = result.size();
        return new AptFinalResponseDto(total, result);
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
