package kh.devspaceapi.model.dto.mypage;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
	private String nickname;
	private String gender;
	private LocalDate birthdate;
	private String email;
}
