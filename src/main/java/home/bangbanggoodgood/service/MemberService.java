package home.bangbanggoodgood.service;

import home.bangbanggoodgood.config.JwtTokenProvider;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.domain.Statistics;
import home.bangbanggoodgood.dto.*;
import home.bangbanggoodgood.repository.MemberRepository;
import home.bangbanggoodgood.repository.StatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;

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

        updateStatistics("sex", requestDto.getSex());
        updateStatistics("age", getAgeGroup(requestDto.getBirth()));

        return new SignUpResponseDto(members.getId());
    }

    private String getAgeGroup(String birth) {
        int birthYear = Integer.parseInt(birth.substring(0, 4)); // 생년 추출
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - birthYear;

        if (age < 20) return "10대";
        if (age < 30) return "20대";
        if (age < 40) return "30대";
        if (age < 50) return "40대";
        if (age < 60) return "50대";
        if (age < 70) return "60대";
        return "70대 이상";
    }

    private void updateStatistics(String category, String subCategory) {
        Statistics statistics = statisticsRepository.findByCategoryAndSubCategory(category, subCategory)
                .orElseGet(() -> {
                    Statistics newStat = new Statistics();
                    newStat.setCategory(category);
                    newStat.setSubCategory(subCategory);
                    newStat.setCount(0);
                    return newStat;
                });

        System.out.println("카테고리 : " + category + ", 서브 카테고리 : " + subCategory);
        System.out.println("현재 카운트 : " + statistics.getCount());

        statistics.setCount(statistics.getCount() + 1);
        statisticsRepository.save(statistics);
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

    public AdminMemberInfoResponseDto getUserInfoByUseId(AdminMemberInfoRequestDto requestDto) {
        Members members = memberRepository.findByUseId(requestDto.getUseId());
        return new AdminMemberInfoResponseDto(members.getName(), members.getBirth(), members.getSex(), members.getJob(), members.getUseId(), members.getAuthority());
    }
}

