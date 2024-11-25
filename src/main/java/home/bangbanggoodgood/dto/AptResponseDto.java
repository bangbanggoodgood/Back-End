package home.bangbanggoodgood.dto;

import home.bangbanggoodgood.domain.HashTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AptResponseDto {
    String aptSeq;
    String aptNm;
    int buildYear;
    BigDecimal maxArea;
    BigDecimal minArea;
    String address;
    Map<String, Integer> infra;
    List<String> hashtags;
    int maxDealAmount;
    int minDealAmount;
    Long like;

}
