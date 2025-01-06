package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.Statistics;
import home.bangbanggoodgood.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public Map<String, Object> getCategoryStatistics(String category) {
        List<Statistics> categoryStatistics = statisticsRepository.findByCategory(category);
        System.out.println(categoryStatistics);

        // 카운트를 저장할 Map
        Map<String, Integer> subCategoryCounts = new HashMap<>();

        // 각 서브 카테고리별로 카운트 합산
        for (Statistics stat : categoryStatistics) {
            subCategoryCounts.merge(stat.getSubCategory(), stat.getCount(), Integer::sum);  // 서브 카테고리별로 합산
        }

        // 총합을 계산
        int total = subCategoryCounts.values().stream().mapToInt(Integer::intValue).sum();

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.putAll(subCategoryCounts);  // 서브 카테고리별 카운트
        result.put("total", total);  // 총합

        saveCategoryStatistics(category, subCategoryCounts, categoryStatistics);
        return result;
    }

    public void saveCategoryStatistics(String category, Map<String, Integer> subCategoryData, List<Statistics> existingStatistics) {

        // 2. 기존 데이터를 Map으로 변환 (key: subCategory, value: Statistics 객체)
        Map<String, Statistics> existingStatisticsMap = new HashMap<>();
        for (Statistics stat : existingStatistics) {
            existingStatisticsMap.put(stat.getSubCategory(), stat);
        }

        // 3. 새로운 데이터를 처리
        for (Map.Entry<String, Integer> entry : subCategoryData.entrySet()) {
            String subCategory = entry.getKey();
            Integer count = entry.getValue();

            // 기존 데이터가 있으면 업데이트, 없으면 새로 생성
            Statistics stat = existingStatisticsMap.getOrDefault(subCategory, new Statistics());
            stat.setCategory(category);
            stat.setSubCategory(subCategory);
            stat.setCount(count);

            // 저장
            statisticsRepository.save(stat);
        }
    }



}
