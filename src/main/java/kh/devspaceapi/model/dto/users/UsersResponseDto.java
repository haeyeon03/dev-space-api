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
	//총 유저
	private long totalUsers;
	//총 뉴스건수
	private long totalNewsPost;
	//총 게시판 건수
	private long totalBoardPost;
	//총 댓글 건수
	private long totalComments;

	public UsersResponseDto(long usersTotal, long newsTotal, long commentTotal, long boardTotal) {
        this.totalUsers = usersTotal;
        this.totalNewsPost = newsTotal;
        this.totalBoardPost = commentTotal;
        this.totalComments = boardTotal;
    }

}
