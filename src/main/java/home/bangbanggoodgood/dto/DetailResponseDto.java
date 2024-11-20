package home.bangbanggoodgood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DetailResponseDto {
    String dealDate;
    int price;
    BigDecimal area;
    int floor;
}
