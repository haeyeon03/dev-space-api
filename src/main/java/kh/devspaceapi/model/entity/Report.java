package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT")
@Getter
@Setter
@NoArgsConstructor
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId; // 신고 ID (PK)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType; // 신고 대상 종류 (POST, COMMENT, NEWS 등)

    @Column(nullable = false)
    private Long targetId; // 신고 대상 ID (게시글ID, 댓글ID 등)

    @Column(nullable = false, length = 100)
    private String reportReason; // 신고 사유

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Users user;

}