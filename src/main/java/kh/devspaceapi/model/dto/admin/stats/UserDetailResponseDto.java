package kh.devspaceapi.model.dto.admin.stats;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponseDto {
	
	private String userId;
	private String nickname;
	private String gender; // "M" or "F"
	private String role; // "admin" or "user"
	private boolean admin; // role == "admin"
	private boolean banned; // 정지 혹은 활동가능 상태
	private String banReason; //패널티 사유
	private LocalDateTime reportedAt; // 신고가 접수된 시간
	private LocalDateTime banEffectiveAt; // 활동 정지 적용시간
	private Long banDurationSec; // 활동정지 지속시간
	private LocalDateTime banEndAt; // 종료시각

}
