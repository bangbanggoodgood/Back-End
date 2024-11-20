package home.bangbanggoodgood.service;

import home.bangbanggoodgood.repository.AptDealRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DealService {

    private final AptDealRepository aptDealRepository;

    public Map<String, Double> getGraphDetail(String aptSeq) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int startYear = currentYear - 5;

        // AptDeals에 대해 평균 거래액을 가져옵니다.
        List<Tuple> result = aptDealRepository.findAvgDealAmountByMonth(startYear, currentYear, currentMonth, aptSeq);


        // 결과를 담을 Map 생성
        Map<String, Double> map = new TreeMap<>(Collections.reverseOrder());

        // startYear부터 currentYear까지 모든 월에 대해 기본 값 0.0으로 넣어줍니다.
        for (int year = startYear; year <= currentYear; year++) {
            for (int month = 1; month <= 12; month++) {
                String yearMonth = String.format("%04d%02d", year, month);
                map.put(yearMonth, map.getOrDefault(yearMonth, 0.0));
            }
        }

        // 쿼리에서 가져온 result를 Map에 넣어줍니다.
        for (Tuple tuple : result) {
            String yearMonth = tuple.get(0, String.class);  // yearMonth
            Double dealAmount = tuple.get(1, Double.class); // 평균 거래액
            map.put(yearMonth, dealAmount);  // map에 yearMonth를 키로, dealAmount를 값으로 저장
        }

        return map;
    }


}
