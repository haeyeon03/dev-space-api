package kh.devspaceapi.model.dto.mypage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
	private String currentPassword;
	private String newPassword;
}
