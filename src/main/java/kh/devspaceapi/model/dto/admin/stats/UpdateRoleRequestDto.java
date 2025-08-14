package kh.devspaceapi.model.dto.admin.stats;

import lombok.Data;

@Data
public class UpdateRoleRequestDto {

	// 유저권한 변경 dto
	private String role; // "admin" 또는 "user"

}
