package home.bangbanggoodgood.service;

import home.bangbanggoodgood.config.GptConfig;
import home.bangbanggoodgood.dto.QnARequestDto;
import home.bangbanggoodgood.dto.QnAResponseDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QnAService {

    private final GptConfig gptConfig;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private String makePrompt(String question) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("너는 부동산 관련 도메인 지식이 아주 뛰어난 사람이야. \n" +
                        "앞으로 사용자가 궁금한 것들을 물어볼텐데 넌 다 친절하게 답 해줄 수 있을거야 \n" +
                        "이제 사용자의 질문을 줄게 + \n"
                        + "대답은 최대한 친절하게, 그리고 요점이 있게 말하면 좋겠어. 너무 길어도 안돼 2-3줄 이내면 좋아. 최대한 쉽게 설명 해줘 " + "\n");

        prompt.append("질문 : " + question);
        return prompt.toString();
    }


    public QnAResponseDto askToAI(QnARequestDto dto) {
        String prompt = makePrompt(dto.getQuestion());  // prompt 생성

        HttpHeaders headers = gptConfig.httpHeaders();  // HTTP 헤더 설정

        // 요청 본문 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", gptConfig.getModel());
        System.out.println("use Model : " + gptConfig.getModel());
        requestBody.put("messages", new Object[]{
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "너는 부동산 관련 지식이 아주 많아. 지금부터 사용자들이 질문하는 것을 다 친절히 답할거야");
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
        return new QnAResponseDto(content);
    }
}
