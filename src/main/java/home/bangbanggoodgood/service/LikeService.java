package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.LikeDto;
import home.bangbanggoodgood.dto.LikeRequestDto;
import home.bangbanggoodgood.dto.LikeResponseDto;
import home.bangbanggoodgood.repository.AptRepository;
import home.bangbanggoodgood.repository.LikeRepository;
import home.bangbanggoodgood.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
