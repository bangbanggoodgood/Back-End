package home.bangbanggoodgood.dto;

import home.bangbanggoodgood.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminMemberInfoResponseDto {
    String name;
    String birth;
    String sex;
    String job;
    String useId;
    Authority role;
}
