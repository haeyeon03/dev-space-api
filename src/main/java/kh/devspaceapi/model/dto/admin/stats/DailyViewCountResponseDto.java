package kh.devspaceapi.model.dto.admin.stats;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyViewCountResponseDto {
	
	// date타입을 문자열로 출력"2025-08-08"
	private String date;
	//조회수
	private Long viewCount;
	
	
	
}
