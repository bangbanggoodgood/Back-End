package home.bangbanggoodgood.service;

import home.bangbanggoodgood.config.JwtTokenProvider;
import home.bangbanggoodgood.domain.Authority;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.domain.Statistics;
import home.bangbanggoodgood.dto.*;
import home.bangbanggoodgood.repository.MemberRepository;
import home.bangbanggoodgood.repository.StatisticsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;

    public SignUpResponseDto signUp(MemberSignUpRequestDto requestDto, Long memberId) {
        Members member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        if (!isValidId(requestDto.getUseId())) {
            throw new RuntimeException("존재하는 아이디 입니다.");
        }
        member.setJob(requestDto.getJob());
        member.setBirth(requestDto.getBirth());
        member.setName(requestDto.getName());
        member.setUseId(requestDto.getUseId());
        member.setSex(requestDto.getSex());
        member.setIsSurvey(true);
        memberRepository.save(member);

        updateStatistics("sex", requestDto.getSex());
        updateStatistics("age", getAgeGroup(requestDto.getBirth()));

        return new SignUpResponseDto(member.getId());
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
        // 해당 카테고리에 대한 모든 통계 데이터를 한번에 조회한다.
        List<Statistics> statisticsList = statisticsRepository.findByCategory(category);

        Statistics statistics = statisticsList.stream().filter(s -> s.getSubCategory().equals(subCategory)).findFirst()
                .orElseGet(() -> {
                    Statistics newStatistics = new Statistics();
                    newStatistics.setCategory(category);
                    newStatistics.setSubCategory(subCategory);
                    newStatistics.setCount(0);
                    statisticsList.add(newStatistics);
                    return newStatistics;
                });
        statistics.setCount(statistics.getCount() + 1);

        statisticsRepository.save(statistics);
    }

    private boolean isValidId(String useId) {
        return !memberRepository.existsByUseId(useId);
    }

    public MemberInfoResponseDto getUserInfo(Long memberId) {
        Members member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        MemberInfoResponseDto memberInfoResponseDto = new MemberInfoResponseDto(
                member.getName(), member.getBirth(), member.getSex(), member.getJob(), member.getUseId()
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

    public AdminMemberInfoResponseDto changeAuthority(AdminMemberInfoRequestDto requestDto) {
        // useId로 해당 Member를 조회합니다.
        Members members = memberRepository.findByUseId(requestDto.getUseId());

        if (members != null) {
            // 현재 권한을 가져옵니다.
            Authority auth = members.getAuthority();

            // 권한이 ADMIN이면 USER로 변경
            if (auth.equals(Authority.ADMIN)) {
                members.setAuthority(Authority.USER);  // USER로 권한 변경
            }
            // 권한이 USER이면 ADMIN으로 변경
            else if (auth.equals(Authority.USER)) {
                members.setAuthority(Authority.ADMIN);  // ADMIN으로 권한 변경
            }

            // 변경된 권한을 DB에 저장합니다.
            memberRepository.save(members);

            // 변경된 정보를 DTO로 반환
            return new AdminMemberInfoResponseDto(members.getName(), members.getBirth(), members.getSex(), members.getJob(), members.getUseId(), members.getAuthority());
        } else {
            // members가 null인 경우 처리 (예: 해당 userId가 존재하지 않음)
            throw new EntityNotFoundException("User not found with useId: " + requestDto.getUseId());
        }
    }

}

