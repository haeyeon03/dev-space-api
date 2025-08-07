package kh.devspaceapi.model.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenderRatioResponseDto {
	//users 남여 성별비율 countDto
	private long maleCounts;
	private long femaleCounts;
}
