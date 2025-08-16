package kh.devspaceapi.model.dto.admin.stats;

import java.time.LocalDateTime;

import kh.devspaceapi.model.entity.enums.ReportStatus;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDetailDto {
	
	// 신고 list 상세확인 DTO
	
	private Long reportId;
	
	// targetType과 같이 활용하여 신고대상 판별
    private TargetType targetType;
    private Long targetId;
    
    //신고된 content 제목(댓글이면 댓글 초반내용)
    private String contentTitle;
    
    // 상세용: 본문 일부 (댓글이면 댓글 내용)
    private String contentBodyPreview;
    
    // 신고사유
    private String reason;
    
    // 신고한 유저
    private String reporterUserId;
    
    // 처리상태
    private ReportStatus status;
    
    // 접수시각 BaseEntity의createdAt 활용
    private LocalDateTime reportedAt;
    
    // 처리 담당자
    private String handlerUserId;
    
    // 처리 메모
    private String handlerNote;
    
    // 처리완료 시각
    private LocalDateTime processedAt;

    // 프론트 이동 링크 ex) "/news/123", "/board/99", "/news/123#comment-777"
    private String contentPath;

}
