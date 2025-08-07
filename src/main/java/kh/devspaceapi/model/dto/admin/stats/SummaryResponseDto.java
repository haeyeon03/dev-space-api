package kh.devspaceapi.model.dto.admin.stats;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryResponseDto {
    //총 유저
    private long totalUsers;
    //총 뉴스건수
    private long totalNewsPosts;
    //총 게시판 건수
    private long totalBoardPosts;
    //총 댓글 건수
    private long totalComments;
}
