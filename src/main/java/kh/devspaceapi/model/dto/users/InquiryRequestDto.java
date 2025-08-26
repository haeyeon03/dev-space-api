package kh.devspaceapi.model.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequestDto {
    private String email;
    private String title;
    private String message;
}
