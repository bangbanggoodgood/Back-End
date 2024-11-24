package home.bangbanggoodgood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class MemberInfoResponseDto {
    String name;
    String birth;
    String sex;
    String job;
    String useId;

}
