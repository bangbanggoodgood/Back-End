package home.bangbanggoodgood.service;

import home.bangbanggoodgood.config.GptConfig;
import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.dto.IntroduceResponseDto;
import home.bangbanggoodgood.repository.AptRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IntroduceService {

    private final AptRepository aptRepository;
    private final GptConfig gptConfig;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public IntroduceResponseDto checkDB(String aptSeq) {
        Optional<AptInfos> info = aptRepository.findById(aptSeq);
        String answerComment = null;
        if(info.isPresent()) { // 아파트가 있다
            String comment = aptRepository.findCommentByAptSeq(aptSeq);
            if (comment == null) {
                System.out.println("이거 찍히면 큰일난거임");
                String newComment = sendToGpt(aptSeq);
                AptInfos nowInfo = aptRepository.findByAptSeq(aptSeq);
                nowInfo.updateComment(newComment);
                aptRepository.save(nowInfo);
                answerComment = newComment;
            } else {
                answerComment = comment;
            }
        }
        return new IntroduceResponseDto(answerComment);
    }

    private String generatePrompt(String aptSeq) {
        // 추후에 인프라 추가 예정인 거 제발 잊지 않기
        String localName = getLocalInfo(aptSeq);

        StringBuilder prompt = new StringBuilder();
        prompt.append("너는 너 자신에 대한 정보를 소개하는 아파트가 될거야. \n" +
                        "주변 인프라들 구성과 그 인프라들의 개수, 시/도 정보, 구/군 정보, 동 정보를 줄게 \n" +
                        "그 지역에 관한 좋은 정보나 분위기, 인프라 구성을 보고 자기 소개글을 보내줘 \n" +
                        "활기찬 느낌이었으면 좋겠고, 꼭 존대말을 사용해서 한글로 작성해줘. \n" +
                        "분량은 짧게 1-2줄 정도로 부탁해. \n" +
                        "안녕하세요 는 안해도 돼 ! 바로 본론으로 들어가줘");

        prompt.append(String.format(" 아파트 위치 : %s", localName));

        return prompt.toString();
    }

    private String getLocalInfo(String aptSeq) {
        String localName = aptRepository.findDongCodeByAptSeq(aptSeq);
        return localName;
    }

    private String sendToGpt(String aptSeq) {
        String prompt = generatePrompt(aptSeq);  // prompt 생성

        HttpHeaders headers = gptConfig.httpHeaders();  // HTTP 헤더 설정

        // 요청 본문 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", gptConfig.getModel());
        System.out.println("use Model : " + gptConfig.getModel());
        requestBody.put("messages", new Object[]{
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "너는 너 자신에 대한 정보를 소개하는 아파트가 될거야.");
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", prompt);
                }}
        });

        // REST 요청 보내기 (POST 요청)
        ResponseEntity<String> responseEntity = gptConfig.restTemplate().exchange(
                API_URL,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),  // 요청 본문과 헤더 설정
                String.class  // 응답을 String으로 받음
        );
        String responseBody = responseEntity.getBody();


        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject choice = jsonObject.getJSONArray("choices").getJSONObject(0);
        String content = choice.getJSONObject("message").getString("content");

        int buildYear = aptRepository.findBuildYearByAptSeq(aptSeq);
        String hi = String.format("안녕하세요! 저는 %d년생 아파트 입니다! ", buildYear);
        return hi + content;
    }


}
