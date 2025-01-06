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
    private final TimeDataService timeDataService; // TimeDataService 추가
    private final InfraRepository infraRepository;  // InfraRepository 추가
    private final HashTagsRepository hashTagsRepository; // HashTagsRepository 추가

    public AptFinalResponseDto show(AptRequestDto dto, Long memberId, String presentPage, String limit) {
        List<String> dongCodes;
        String dongCode;
        int targetMinPrice = 0;
        int targetMaxPrice = Integer.MAX_VALUE;
        List<AptResponseDto> result = null;

        // 가격 범위 설정
        if (dto.getTargetMinPrice() != -1) {
            targetMinPrice = dto.getTargetMinPrice();
        }
        if (dto.getTargetMaxPrice() != -1) {
            targetMaxPrice = dto.getTargetMaxPrice();
        }

        if (dto.getDongName().equals("전체")) { // 전체 동에 대해 처리
            dongCodes = infoService.findDongCodes(dto.getSidoName(), dto.getGugunName());
            result = findDealListWithDongCodes(dto, dongCodes, targetMinPrice, targetMaxPrice, memberId);
        } else {
            dongCode = infoService.findDongCode(dto.getSidoName(), dto.getGugunName(), dto.getDongName());
            result = findDealListWithOneDongCode(dto, dongCode, targetMinPrice, targetMaxPrice, memberId);
        }

        // 체류시간이 가장 긴 아파트를 찾고, 해당 아파트 특성을 가져와서 리스트를 정렬
        AptInfos recommendedApt = timeDataService.recommendApts(memberId); // 시간 기반 추천 아파트 찾기
        if (recommendedApt != null) {
            String aptSeq = recommendedApt.getAptSeq();
            String dongCodeForCluster = aptRepository.findDongCodeByAptSeq(aptSeq);
            Long clusterId = aptRepository.findClusterNumByDongCode(dongCodeForCluster);
            Integer[] priceRange = getPriceRangeForApt(aptSeq);

            // 정렬 전 디버깅 로그 추가
            System.out.println("=======================================================================================================");
            System.out.println("Before sorting: " + result);
            System.out.println("=======================================================================================================");

            result.sort((apt1, apt2) -> {
                int priceComparison = compareByPriceRange(apt1, apt2, priceRange);
                if (priceComparison != 0) {
                    return priceComparison;
                }
                return compareByCluster(apt1, apt2, clusterId);
            });

            // 정렬 후 디버깅 로그 추가
            System.out.println("=============================================================아 저거==========================================");
            System.out.println("After sorting: " + result);
            System.out.println("=======================================================================================================");
        } else {
            System.out.println("=======================================================================================================");
            System.out.println("이거 뜨면 Null");
            System.out.println("=======================================================================================================");
        }

        // 페이징 처리: 정렬된 리스트에서 원하는 범위만큼 가져오기
        int presentPageInt = Integer.parseInt(presentPage);  // String -> int로 변환
        int limitInt = Integer.parseInt(limit);  // String -> int로 변환

        int startIndex = (presentPageInt - 1) * limitInt;
        int endIndex = Math.min(startIndex + limitInt, result.size());  // 범위가 리스트 크기를 넘지 않도록 처리
        List<AptResponseDto> pagedResult = result.subList(startIndex, endIndex);

        int total = result.size();
        return new AptFinalResponseDto(total, pagedResult);
    }

    // 아파트의 가격대 비교
    private int compareByPriceRange(AptResponseDto apt1, AptResponseDto apt2, Integer[] priceRange) {
        Integer apt1MinPrice = apt1.getMinDealAmount();
        Integer apt1MaxPrice = apt1.getMaxDealAmount();
        Integer apt2MinPrice = apt2.getMinDealAmount();
        Integer apt2MaxPrice = apt2.getMaxDealAmount();

        // 가격대에 따라 정렬
        int apt1InPriceRange = (apt1MinPrice >= priceRange[0] && apt1MaxPrice <= priceRange[1]) ? 1 : 0;
        int apt2InPriceRange = (apt2MinPrice >= priceRange[0] && apt2MaxPrice <= priceRange[1]) ? 1 : 0;

        if (apt1InPriceRange == apt2InPriceRange) {
            // 가격대가 비슷한 아파트끼리 더 정확히 비교 (추천 아파트와 차이가 적은 아파트 우선)
            int apt1PriceDiff = Math.abs(apt1MinPrice - priceRange[0]) + Math.abs(apt1MaxPrice - priceRange[1]);
            int apt2PriceDiff = Math.abs(apt2MinPrice - priceRange[0]) + Math.abs(apt2MaxPrice - priceRange[1]);
            return Integer.compare(apt1PriceDiff, apt2PriceDiff); // 차이가 적은 가격대 우선
        }

        return Integer.compare(apt2InPriceRange, apt1InPriceRange); // 체류시간이 긴 아파트가 가격대가 일치하면 우선
    }

    // 클러스터 ID 비교
    private int compareByCluster(AptResponseDto apt1, AptResponseDto apt2, Long clusterId) {
        // aptSeq를 사용해서 동코드를 조회
        String apt1DongCode = aptRepository.findDongCodeByAptSeq(apt1.getAptSeq());
        String apt2DongCode = aptRepository.findDongCodeByAptSeq(apt2.getAptSeq());

        Long apt1Cluster = aptRepository.findClusterNumByDongCode(apt1DongCode);
        Long apt2Cluster = aptRepository.findClusterNumByDongCode(apt2DongCode);

        if (apt1Cluster.equals(apt2Cluster)) {
            return 0; // 클러스터 ID가 같으면 동일한 그룹으로 처리
        }

        return Long.compare(apt2Cluster, apt1Cluster); // 클러스터 ID가 높은 순으로 정렬
    }

    // 해당 아파트의 가격대 반환 (최소 가격, 최대 가격)
    private Integer[] getPriceRangeForApt(String aptSeq) {
        Tuple priceTuple = aptRepository.findPriceRangeByAptSeq(aptSeq);
        Integer minPrice = priceTuple.get(0, Integer.class);
        Integer maxPrice = priceTuple.get(1, Integer.class);
        return new Integer[]{minPrice, maxPrice};
    }
    private List<AptResponseDto> findDealListWithOneDongCode(AptRequestDto dto, String dongCode, int targetMinPrice, int targetMaxPrice, Long memberId) {
        List<Tuple> tuples = null;
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

