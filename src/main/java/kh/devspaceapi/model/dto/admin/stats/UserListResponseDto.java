package kh.devspaceapi.model.dto.admin.stats;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserListResponseDto {
	/*
	 * 관리자 유저 리스트를 불러오는 DTO
	 * 
	 * isBanned를 활용하여 뉴스댓글 작성, 게시글 작성, 게시글 댓글작성을
	 * 활동 정지를 시킨다
	 * */
	private String userId;
    private String nickname;
    private String gender;
    private String role; //"admin" | "user"
    private boolean banned; // 정지 여부
    private LocalDateTime banEndAt; // 정지 종료일 (null이면 정지 아님)

}
