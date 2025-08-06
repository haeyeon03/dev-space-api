package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "POST_COMMENT")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "post_comment_seq_gen", sequenceName = "POST_COMMENT_SEQ", allocationSize = 1)
public class PostComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "post_comment_seq_gen")
    private Long postCommentId;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Users user;
    private Long targetId; // FK (NewsPostId or BoardPostId)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;
}
