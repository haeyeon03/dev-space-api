package kh.devspaceapi.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import kh.devspaceapi.model.entity.base.BaseEntity;
import kh.devspaceapi.model.entity.enums.ReportStatus;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONTENT_REPORT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "content_report_seq_gen", sequenceName = "CONTENT_REPORT_SEQ", allocationSize = 1)
public class ContentReport extends BaseEntity{

	// ContentReport의 pk
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_report_seq_gen")
	private Long reportId;
	
	// 신고대상 content. targetType과 같이 활용하여 신고대상 판별
	private Long targetId;
	
	// NEWS / BOARD / COMMENT
	@Enumerated(EnumType.STRING)
    private TargetType targetType;
	
	// 신고한 유저
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_USER_ID", referencedColumnName = "USER_ID")
	private Users reporter;
	
	// 신고 사유
	@Column(nullable = false, length = 1000)
    private String reason;
	
	// 처리상태
	@Enumerated(EnumType.STRING)
    private ReportStatus status; // 기본 PROCESSING, 처리완료후 COMPLETED
	
	// 처리 담당자
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HANDLER_USER_ID", referencedColumnName = "USER_ID")
    private Users handler; // null 가능
	
	// 처리 메모
	private String handlerNote;
	
	// 처리완료 시각
	private LocalDateTime processedAt;
}
