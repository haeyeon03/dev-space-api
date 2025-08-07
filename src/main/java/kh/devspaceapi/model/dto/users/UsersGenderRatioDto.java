package kh.devspaceapi.model.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersGenderRatioDto {
	//users 남여 성별비율 countDto
	private long maleCount;
	private long femaleCount;
	

}
