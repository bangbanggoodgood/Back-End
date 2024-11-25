package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.domain.Infras;
import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import home.bangbanggoodgood.dto.*;
import home.bangbanggoodgood.repository.*;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository repository;
    private final MemberRepository memberRepository;
    private final AptRepository aptRepository;
    private final InfoRepository infoRepository;
    private final LikeRepository likeRepository;
    private final InfraRepository infraRepository;
    private final HashTagsRepository hashTagRepository;  // 해시태그 관련 리포지토리 추가

    public LikeResponseDto postLike(Long memberId, LikeRequestDto requestDto) {
        Members members = memberRepository.findMemberById(memberId);
        AptInfos infos = aptRepository.findByAptSeq(requestDto.getAptSeq());
        Optional<Likes> likes = repository.findByMemberAndAptInfo_AptSeq(members, requestDto.getAptSeq());
        if (likes.isEmpty()) {
            repository.save(LikeDto.toEntity(members, infos));
            infos.updateCount(1L);
        } else {
            repository.deleteById(likes.get().getId());
            infos.updateCount(0L);
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

        for (Tuple tuple : tuples) {
            // 아파트에 해당하는 동 코드 목록을 가져옵니다.
            String aptSeq = tuple.get(0, String.class);  // aptSeq
            String dongCode = aptRepository.findDongCodeByAptSeq(aptSeq); // 동 코드 목록을 가져옴
            Long clusterId = aptRepository.findClusterNumByDongCode(dongCode);
            // 동 코드별로 인프라 정보를 조회
            Infras infra = infraRepository.findByDongCode(Long.valueOf(dongCode));
            List<Infras> infraList = Collections.singletonList(infra);  // 단일 객체를 리스트로 변환
            Map<String, Integer> infraResult = getInfraMap(infraList);  // 수정된 코드

            // 좋아요 여부 확인
            Long like = isLikeNow(aptSeq, memberId);

            // 해시태그 처리
            List<String> hashtags = hashTagRepository.findHashTagsById(clusterId + 1);

            // AptResponseDto를 생성하여 결과 목록에 추가
            result.add(new AptResponseDto(
                    aptSeq,  // aptSeq
                    tuple.get(1, String.class),  // aptNm
                    tuple.get(2, Integer.class), // buildYear
                    tuple.get(3, BigDecimal.class), // maxArea
                    tuple.get(4, BigDecimal.class), // minArea
                    tuple.get(5, String.class),  // address
                    infraResult,  // 인프라 정보 Map 추가
                    hashtags,  // 해시태그 목록 추가
                    tuple.get(6, Integer.class), // maxDealAmount
                    tuple.get(7, Integer.class), // minDealAmount
                    like  // 좋아요 여부
            ));
        }
        return result;
    }

    private List<Tuple> findSidoAndGugun(Long memberId) {
        List<String> dongCodes = repository.findDongCodeByAptInfos(memberId);
        List<Tuple> tuples = new ArrayList<>();

        // 동 코드 목록을 기반으로 해당하는 Sido, Gugun을 찾고, 이를 기반으로 아파트 정보를 조회
        for (String dong : dongCodes) {
            String sidoName = infoRepository.findSidoNameByDongCode(dong);
            String gugunName = infoRepository.findGugunNameByDongCodeAndSidoName(sidoName, dong);
            String sidoGugun = sidoName + " " + gugunName;

            tuples = repository.findAptInfosByMemberId(memberId, sidoGugun);
        }

        return tuples;
    }

    private Long isLikeNow(String aptSeq, Long memberId) {
        Members members = memberRepository.findMemberById(memberId);
        Optional<Likes> likes = likeRepository.findByMemberAndAptInfo_AptSeq(members, aptSeq);
        return likes.isEmpty() ? 0L : 1L;
    }

    private Map<String, Integer> getInfraMap(List<Infras> infraList) {
        Map<String, Integer> infraMap = new HashMap<>();

        // 각 인프라 항목을 Map에 추가
        for (Infras infra : infraList) {
            // 학교 관련 인프라 합치기
            int totalSchools = infra.getHighSchools() + infra.getMiddleSchools() + infra.getElementarySchools();
            infraMap.put("schools", totalSchools);

            // 학원 정보
            infraMap.put("academy", infra.getAcademies());

            // 의료 인프라
            int totalHos = infra.getHospitals() + infra.getPharmacies();
            infraMap.put("healthCare", totalHos);

            // 마트 관련 인프라
            int totalMart = infra.getSupermarkets() + infra.getConvenienceStores();
            infraMap.put("convinience", totalMart);

            // 음식점, 카페, 술집 정보
            infraMap.put("restaurant", infra.getRestaurants());
            infraMap.put("cafe", infra.getCafes());
            infraMap.put("pubs", infra.getPubs());

            // 여가 관련 인프라
            int play = infra.getEntertainmentServices() + infra.getSportsService();
            infraMap.put("leisure", play);

            // 교통 인프라
            infraMap.put("bus", infra.getBuses());
            infraMap.put("subway", infra.getSubways());
            infraMap.put("petHospital", infra.getAnimalHospital());
        }

        return infraMap;
    }

}
