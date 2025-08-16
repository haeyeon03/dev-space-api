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
public class ReportListDto {
	
	// 신고 list DTO
	
	private Long reportId;
	
	// targetType과 같이 활용하여 신고대상 판별
	private Long targetId;
    private TargetType targetType;
    
    // 대상 콘텐츠 제목/요약
    private String contentTitle;
    
    // 신고 사유
    private String reason;
    
    // 신고자
    private String reporterUserId;
    
    // 기본 PROCESSING, 처리완료후 COMPLETED
    private ReportStatus status;
    
    // 접수시각 BaseEntity의createdAt 활용
    private LocalDateTime reportedAt;
    
    // 프론트 이동 링크 ex) "/news/123", "/board/99", "/news/123#comment-777"
    private String contentPath;      

}
