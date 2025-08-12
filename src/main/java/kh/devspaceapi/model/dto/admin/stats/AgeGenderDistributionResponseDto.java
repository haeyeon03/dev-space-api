package kh.devspaceapi.model.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgeGenderDistributionResponseDto {
	
	// 연령대별 그룹 ex) 10대, 20대...
		private String ageGroup;
		// M, F
	    private String gender;
	    // 조회 수
	    private Long viewCount;

}
