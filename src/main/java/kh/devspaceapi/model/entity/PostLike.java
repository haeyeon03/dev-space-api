package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "POST_LIKE")
@Getter
@Setter
@NoArgsConstructor

public class PostLike extends BaseEntity {
    @Id

    private Long postLikeId; // 데이터 특성상 '반드시' deletion 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Users user;

    private Long targetId; // FK (NewsPostId or BoardPostId)

    @Enumerated(EnumType.STRING)
    private TargetType targetType;
}
