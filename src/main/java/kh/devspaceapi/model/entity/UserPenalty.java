package kh.devspaceapi.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_PENALTY")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "user_penalty_seq_gen", sequenceName = "POST_LIKE_SEQ", allocationSize = 1)
public class UserPenalty extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_penalty_seq_gen")
    private int userPenaltyId; // 패널티 ID (PK)
    private String reason; // 신고 사유
    // 사용자가 신고를 접수한 날짜 및 시간 (필수 입력)
    @Column(name = "REPORTED_AT", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedAt; // 신고가 접수된 시간
    private LocalDateTime effectiveAt; // 패널티 적용 시간
    private long duration_sec; // 기간(Sec.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Users user;

}