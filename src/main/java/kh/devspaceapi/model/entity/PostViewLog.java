package kh.devspaceapi.model.entity;

import java.sql.Timestamp;

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
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "POST_VIEW_LOGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "post_view_count_seq_gen", sequenceName = "POST_VIEW_COUNT_SEQ", allocationSize = 1)
public class PostViewLog {
	
	// PostViewLog의 pk
	// BoardPost, NewsPost 조회수 표시기능 추가를 위해 만든 엔티티
	// PostComment의 조회수는 관리자 대시보드 데이터 집계용으로 사용
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "post_view_count_seq_gen")
	private Long postViewId;
	/*
	 * board, news, comment의 pk가 들어오는 컬럼
	 * targetType과 함께 활용하여 실제 대상을 구분하는 fk와 같은 기능을 실현
	 */
	private Long targetId; 
	@Enumerated(EnumType.STRING)
    private TargetType targetType;
	// 로그인 사용자 ID fk (비회원은 null 또는 "guest"로 설정)
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USER_ID", referencedColumnName = "USER_ID")
	private Users userId;
	//초회 발생 일시
	private Timestamp viewDate;

}
