package kh.devspaceapi.model.dto.mypage;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyProfileResponseDto {
	private String userId;
	private String nickname;
	private String email;
	private String gender;
	private LocalDate birthdate;
	private String profileImageUrl; // (= Users.profileImageUrl 가정)
}
