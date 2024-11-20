package home.bangbanggoodgood.dto;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import lombok.Data;

@Data
public class LikeDto {
    Members members;
    AptInfos aptInfos;

    public static Likes toEntity(Members members, AptInfos aptInfo) {
        return Likes.builder()
                .member(members)
                .aptInfo(aptInfo)
                .build();
    }
}
