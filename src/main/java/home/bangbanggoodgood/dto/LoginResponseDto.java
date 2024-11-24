package home.bangbanggoodgood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String id;
    private String accessToken;
}
