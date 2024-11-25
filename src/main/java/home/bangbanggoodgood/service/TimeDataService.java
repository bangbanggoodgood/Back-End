package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.dto.TimeRequestDto;
import home.bangbanggoodgood.repository.AptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeDataService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AptRepository aptRepository;

    public void saveTimeData(TimeRequestDto requestDto, Long memberId) {
        String redisKey = "user:" + memberId +":time";

        // 기존 데이터 조회
        Map<String, Integer> timeData = (Map<String, Integer>) redisTemplate.opsForValue().get(redisKey);
        if(timeData == null) {
            timeData = new HashMap<>();
        }


        //체류시간 누적 ( 같은 아파트에 대해 )
        timeData.merge(requestDto.getAptSeq(), requestDto.getTime(), Integer::sum);

        // redis에 저장
        redisTemplate.opsForValue().set(redisKey, timeData);
    }

    public AptInfos recommendApts(Long memberId) {
        String redisKey = "user:" + memberId + ":time";
        Map<String, Integer> timeData = (Map<String, Integer>) redisTemplate.opsForValue().get(redisKey);
        System.out.println("============================================ 호출 돼 ? ==========================================================");
        if (timeData == null || timeData.isEmpty()) {
            System.out.println("No time data for member: " + memberId); // 로그 추가
            return null;
        }

        Optional<Map.Entry<String, Integer>> maxEntry = timeData.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            String key = maxEntry.get().getKey();
            System.out.println("Recommended aptSeq: " + key); // 로그 추가
            return aptRepository.findByAptSeq(key);
        } else {
            System.out.println("No max entry found in time data"); // 로그 추가
            return null;
        }
    }


}
