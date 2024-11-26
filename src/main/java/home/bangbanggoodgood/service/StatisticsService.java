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
         
        return result;
    }


}
