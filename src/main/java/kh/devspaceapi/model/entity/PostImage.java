package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "POST_IMAGE")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "image_seq_gen", sequenceName = "NEWS_SEQ", initialValue = 1, allocationSize = 1)
public class PostImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "image_seq_gen")
    private Long imageId;
    private String path;
    private Long targetId; // FK (NewsPostId or BoardPostId)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Users user;
}
