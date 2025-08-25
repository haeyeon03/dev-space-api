package kh.devspaceapi.model.dto.users;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersRequestDto {
    private String userId;
    private String password;
    private String nickname;
    private String gender;
    private LocalDate birthdate;
    private String email;
    private String provider;
}
