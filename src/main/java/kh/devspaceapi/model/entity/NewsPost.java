package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "NEWS_POST")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "news_post_seq_gen", sequenceName = "NEWS_POST_SEQ", allocationSize = 1)
public class NewsPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_post_seq_gen")
    private Long newsPostId;
    private String sourceName; // source.name
    private String author;
    private String title;

    @Lob
    private String description;

    @Column(length = 500)
    private String url;

    @Column(length = 500)
    private String urlToImage;

    private LocalDateTime publishedAt;

    @Lob
    private String content;
    
}
