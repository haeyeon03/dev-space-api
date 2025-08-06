package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NEWS_POST")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "news_post_seq_gen", sequenceName = "NEWS_POST_SEQ", allocationSize = 1)
public class NewsPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "news_post_seq_gen")
    private Long newsPostId;
    private String title;
    @Lob
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private Users user;
}
