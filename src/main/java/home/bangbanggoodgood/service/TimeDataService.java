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

    // 체류시간 기반으로 추천할 아파트 리스트 조회
    public AptInfos recommendApts(Long memberId) {
        String redisKey = "user:" + memberId +":time";
        Map<String, Integer> timeData = (Map<String, Integer>) redisTemplate.opsForValue().get(redisKey);

        if(timeData == null || timeData.isEmpty()) {
            return null; // 체류시간 데이터가 없으면 Null
        }
        // 체류 시간이 가장 긴 아파트의 시퀀스를 찾아서 반환
        Optional<Map.Entry<String, Integer>> maxEntry = timeData.entrySet().stream()
                .max(Map.Entry.comparingByValue()); // 체류시간이 가장 긴 아파트 찾기

        if(maxEntry.isPresent()) {
            String key = maxEntry.get().getKey();
            return aptRepository.findByAptSeq(key);
        } else {
            return null;
        }
    }
}
