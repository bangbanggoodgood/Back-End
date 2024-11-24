package home.bangbanggoodgood.dto;

import lombok.Data;

@Data
public class MemberSignUpRequestDto {
    String name;
    String birth;
    String sex;
    String job;
    String useId;
}
