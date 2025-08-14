package kh.devspaceapi.model.dto.admin.stats;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApplyPenaltyRequestDto {
	
	private String reason; // 사유(필수입력 2글자 이상)
	private LocalDateTime effectiveAt;   // 적용 시작 시각(필수)
	private long durationSec;            // 정지 기간(초, 0 이상)


}
