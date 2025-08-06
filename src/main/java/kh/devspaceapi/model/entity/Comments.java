package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMMENTS")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "comments_seq_gen", sequenceName = "COMMENTS_SEQ", initialValue = 1, allocationSize = 1)
public class Comments extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Users user;

    private Long targetId; // FK (NewsPostId or BoardPostId)

    @Enumerated(EnumType.STRING)
    private TargetType targetType;
}
