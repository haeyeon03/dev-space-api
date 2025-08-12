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
@SequenceGenerator(name = "post_image_seq_gen", sequenceName = "POST_IMAGE_SEQ", allocationSize = 1)
public class PostImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "post_image_seq_gen")
    private Long postImageId;
    private String filename;
    private Long targetId; // FK (NewsPostId or BoardPostId)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;
}
