package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.Statistics;
import home.bangbanggoodgood.repository.AptDealRepository;
import home.bangbanggoodgood.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class PriceCategoryService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AptDealRepository aptDealRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> categorizeMembersByPrice() {
        // 카테고리 초기화
        Map<String, Integer> priceCategoryMap = initializePriceCategoryMap();

        // Redis에서 키 조회
        Set<String> memberKeys = redisTemplate.keys("user:*:time");
        if (memberKeys == null) return Collections.emptyMap();

        for (String memberKey : memberKeys) {
            String jsonValue = redisTemplate.opsForValue().get(memberKey);
            if (jsonValue == null) continue;

            try {
                // JSON 파싱
                Map<String, Integer> aptSeqData = objectMapper.readValue(jsonValue, HashMap.class);

                for (Map.Entry<String, Integer> entry : aptSeqData.entrySet()) {
                    String aptSeq = entry.getKey();
                    categorizePriceForAptSeq(aptSeq, priceCategoryMap);
                }
            } catch (Exception e) {
                e.printStackTrace(); // JSON 파싱 오류 처리
            }
        }

        // DB 반영
        updateStatisticsInDatabase(priceCategoryMap);

        // 결과 반환
        int total = priceCategoryMap.values().stream().mapToInt(Integer::intValue).sum();

        // 결과 맵 생성
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);

        // 카테고리별 값을 추가 (정렬 순서대로)
        List<String> sortedCategories = Arrays.asList("3억 이하", "3 - 4억", "4 - 5억", "5 - 6억", "6 - 7억", "7억 이상");
        for (String category : sortedCategories) {
            result.put(category, priceCategoryMap.getOrDefault(category, 0));
        }

        return result;
    }

    private Map<String, Integer> initializePriceCategoryMap() {
        Map<String, Integer> priceCategoryMap = new HashMap<>();
        priceCategoryMap.put("3억 이하", 0);
        priceCategoryMap.put("3 - 4억", 0);
        priceCategoryMap.put("4 - 5억", 0);
        priceCategoryMap.put("5 - 6억", 0);
        priceCategoryMap.put("6 - 7억", 0);
        priceCategoryMap.put("7억 이상", 0);
        return priceCategoryMap;
    }

    private void categorizePriceForAptSeq(String aptSeq, Map<String, Integer> priceCategoryMap) {
        // 평균 거래 금액 조회
        Double avgPrice = aptDealRepository.findAveragePriceByAptSeq(aptSeq);

        if (avgPrice == null) {
            System.out.println("No deal data for aptSeq: " + aptSeq);
            return;
        }

        System.out.println("Average Price for aptSeq " + aptSeq + ": " + avgPrice);

        // 평균 거래 금액에 10,000 곱하기
        avgPrice *= 10_000;

        // 카테고리에 따라 분류
        mapAveragePriceToCategory(avgPrice, priceCategoryMap);
    }

    private void mapAveragePriceToCategory(double avgPrice, Map<String, Integer> priceCategoryMap) {
        if (avgPrice <= 300_000_000) {
            priceCategoryMap.put("3억 이하", priceCategoryMap.get("3억 이하") + 1);
        } else if (avgPrice <= 400_000_000) {
            priceCategoryMap.put("3 - 4억", priceCategoryMap.get("3 - 4억") + 1);
        } else if (avgPrice <= 500_000_000) {
            priceCategoryMap.put("4 - 5억", priceCategoryMap.get("4 - 5억") + 1);
        } else if (avgPrice <= 600_000_000) {
            priceCategoryMap.put("5 - 6억", priceCategoryMap.get("5 - 6억") + 1);
        } else if (avgPrice <= 700_000_000) {
            priceCategoryMap.put("6 - 7억", priceCategoryMap.get("6 - 7억") + 1);
        } else {
            priceCategoryMap.put("7억 이상", priceCategoryMap.get("7억 이상") + 1);
        }
    }

    private void updateStatisticsInDatabase(Map<String, Integer> priceCategoryMap) {
        for (Map.Entry<String, Integer> entry : priceCategoryMap.entrySet()) {
            String subCategory = entry.getKey();
            Integer count = entry.getValue();

            // 가격 카테고리 통계 조회
            Statistics stat = statisticsRepository.findByCategoryAndSubCategory("price", subCategory)
                    .orElseGet(() -> {
                        Statistics newStat = new Statistics();
                        newStat.setCategory("price");
                        newStat.setSubCategory(subCategory);
                        newStat.setCount(0);
                        return newStat;
                    });

            stat.setCount(count);
            statisticsRepository.save(stat);
        }
    }
}

