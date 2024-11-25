package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.*;
import home.bangbanggoodgood.dto.AptFinalResponseDto;
import home.bangbanggoodgood.dto.AptRequestDto;
import home.bangbanggoodgood.dto.AptResponseDto;
import home.bangbanggoodgood.repository.*;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AptService {

    private final AptRepository aptRepository;
    private final InfoService infoService;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final InfraRepository infraRepository;
    private final ClusterRepository clusterRepository;
    private final HashTagsRepository hashTagsRepository;

    public AptFinalResponseDto show(AptRequestDto dto, Long memberId) {
        List<String> dongCodes;
        String dongCode;
        int targetMinPrice = 0;
        int targetMaxPrice = Integer.MAX_VALUE;
        List<AptResponseDto> result = null;

        // 가격 범위 설정
        if(dto.getTargetMinPrice() != -1) {
            targetMinPrice = dto.getTargetMinPrice();
        }
        if(dto.getTargetMaxPrice() != -1) {
            targetMaxPrice = dto.getTargetMaxPrice();
        }

        // 동 코드가 '전체'일 때
        if(dto.getDongName().equals("전체")) {
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

        // 아파트 이름이 있는 경우와 없는 경우로 분리하여 쿼리
        if(dto.getAptName() != null) {
            tuples = aptRepository.findByDongAndAptName(dto.getSidoName(), dto.getGugunName(), dongCode, dto.getAptName(), targetMinPrice, targetMaxPrice);
        } else {
            tuples = aptRepository.findByDong(dto.getSidoName(), dto.getGugunName(), dongCode, targetMinPrice, targetMaxPrice);
        }

        List<AptResponseDto> result = getResult(tuples, dongCode, memberId);
        return result;
    }

    private List<AptResponseDto> findDealListWithDongCodes(AptRequestDto dto, List<String> dongCodes, int targetMinPrice, int targetMaxPrice, Long memberId) {
        List<Tuple> tuples = null;

        // 아파트 이름이 있는 경우와 없는 경우로 분리하여 쿼리
        if(dto.getAptName() != null) {
            tuples = aptRepository.findBySidoAndGugunAndAptName(dto.getSidoName(), dto.getGugunName(), dongCodes, dto.getAptName(), targetMinPrice, targetMaxPrice);
        } else {
            tuples = aptRepository.findBySidoAndGugun(dto.getSidoName(), dto.getGugunName(), dongCodes, targetMinPrice, targetMaxPrice);
        }

        List<AptResponseDto> result = getResult(tuples, dongCodes, memberId);
        return result;
    }

    private List<AptResponseDto> getResult(List<Tuple> tuples, Object dongCodeInput, Long memberId) {
        List<AptResponseDto> result = new ArrayList<>();
        List<Long> dongCodesAsLong = new ArrayList<>();

        // dongCodeInput이 List일 경우와 String일 경우를 구분
        if (dongCodeInput instanceof List<?>) {
            List<String> dongCodes = (List<String>) dongCodeInput;
            dongCodesAsLong = dongCodes.stream()
                    .map(Long::parseLong) // String -> Long 변환
                    .collect(Collectors.toList());
        } else if (dongCodeInput instanceof String) {
            dongCodesAsLong.add(Long.parseLong((String) dongCodeInput)); // String -> Long 변환
        }

        for (Tuple tuple : tuples) {
            // 동 코드별로 인프라 정보를 조회
            List<Infras> infraList = infraRepository.findByDongCodeIn(dongCodesAsLong); // 변환된 dongCodes 사용
            Map<String, Integer> infraResult = getInfraMap(infraList);

            // 각 아파트에 대해 필요한 정보를 설정
            Long like = isLikeNow(tuple.get(0, String.class), memberId);

            // 1. 동 코드에 맞는 클러스터 번호를 찾아서
            String aptSeq = tuple.get(0, String.class);
            String dongCode = aptRepository.findDongCodeByAptSeq(aptSeq);
            Long clusterId = aptRepository.findClusterNumByDongCode(dongCode); // 동 코드에 맞는 클러스터 ID 찾기

            // 해당 클러스터 ID에 맞는 해시태그만 가져오기
            System.out.println("clusterId : " + clusterId);
            List<String> hashtags = hashTagsRepository.findHashTagsById(clusterId + 1);
            System.out.println("hashtags : " + hashtags);
            // 해시태그는 이미 List<String> 형태이므로 그대로 사용하면 됩니다.
            result.add(new AptResponseDto(
                    aptSeq, // aptSeq
                    tuple.get(1, String.class),  // aptNm
                    tuple.get(2, Integer.class), // buildYear
                    tuple.get(3, BigDecimal.class), // maxArea
                    tuple.get(4, BigDecimal.class), // minArea
                    tuple.get(5, String.class),  // address
                    infraResult,  // 인프라 정보
                    hashtags, // 해시태그 문자열 리스트 그대로 전달
                    tuple.get(6, Integer.class), // maxDealAmount
                    tuple.get(7, Integer.class), // minDealAmount
                    like // 좋아요 여부
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

    private Map<String, Integer> getInfraMap(List<Infras> infraList) {
        Map<String, Integer> infraMap = new HashMap<>();
        for (Infras infra : infraList) {
            int totalSchools = infra.getHighSchools() + infra.getMiddleSchools() + infra.getElementarySchools();
            infraMap.put("schools", totalSchools);
            infraMap.put("academy", infra.getAcademies());

            int totalHos = infra.getHospitals() + infra.getPharmacies();
            infraMap.put("healthCare", totalHos);

            int totalMart = infra.getSupermarkets() + infra.getConvenienceStores();
            infraMap.put("convinience", totalMart);

            infraMap.put("restaurant", infra.getRestaurants());
            infraMap.put("cafe", infra.getCafes());
            infraMap.put("pubs", infra.getPubs());

            int play = infra.getEntertainmentServices() + infra.getSportsService();
            infraMap.put("leisure", play);

            infraMap.put("bus", infra.getBuses());
            infraMap.put("subway", infra.getSubways());
            infraMap.put("petHospital", infra.getAnimalHospital());
        }
        return infraMap;
    }



}