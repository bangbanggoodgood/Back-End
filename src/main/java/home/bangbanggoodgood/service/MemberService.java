package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.*;
import home.bangbanggoodgood.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public SignUpResponseDto signUp(MemberSignUpRequestDto requestDto, Long memberId) {
        Optional<Members> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new RuntimeException("존재하지 않는 유저입니다.");
        }
        if (!isValidId(requestDto.getUseId())) {
            throw new RuntimeException("존재하는 아이디 입니다.");
        }
        Members members = memberRepository.findMemberById(memberId);
        members.setJob(requestDto.getJob());
        members.setBirth(requestDto.getBirth());
        members.setName(requestDto.getName());
        members.setUseId(requestDto.getUseId());
        members.setSex(requestDto.getSex());
        members.setIsSurvey(true);
        memberRepository.save(members);

        return new SignUpResponseDto(members.getId());
    }

    private boolean isValidId(String useId) {
        return !memberRepository.existsByUseId(useId);
    }

    public MemberInfoResponseDto getUserInfo(Long memberId) {
        Optional<Members> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new RuntimeException("존재하지 않는 유저입니다.");
        }

        Members members = memberRepository.findMemberById(memberId);
        MemberInfoResponseDto memberInfoResponseDto = new MemberInfoResponseDto(
                members.getName(), members.getBirth(), members.getSex(), members.getJob(), members.getUseId()
        );

        return memberInfoResponseDto;
    }

    public void deleteMember(Long memberId) {
        Optional<Members> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new RuntimeException("존재하지 않는 유저입니다.");
        }

        memberRepository.deleteById(memberId);

    }

    public CheckResponseDto checkVaildId(CheckRequestDto checkRequestDto) {
        System.out.println("아이디 : " + checkRequestDto.getUseId());
        boolean isValidId = isValidId(checkRequestDto.getUseId());
        return new CheckResponseDto(isValidId);
    }
}


