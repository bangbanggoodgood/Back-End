package home.bangbanggoodgood.dto;

import home.bangbanggoodgood.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CustomMemberInfoDto {
    private Long memberId;
    private String socialId;
    private Authority authority;
}
