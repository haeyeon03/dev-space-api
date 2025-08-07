package kh.devspaceapi.model.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponseDto {
    private String userId;
    private String nickname;
    private String gender;
    private String email;
    private String role;
    private LocalDate birthdate;
}
