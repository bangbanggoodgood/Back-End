package home.bangbanggoodgood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DetailFinalResponseDto {
    int totalRow;
    List<DetailResponseDto> data;
}
