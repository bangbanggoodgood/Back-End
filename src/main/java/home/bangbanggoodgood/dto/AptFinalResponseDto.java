package home.bangbanggoodgood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AptFinalResponseDto {
    int totalRow;
    List<AptResponseDto> aptDto;
}
