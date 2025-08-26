package kh.devspaceapi.model.entity;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private String title;
    @Lob
    private String content;
    @Column(unique = true)
    private String url;
    @ElementCollection
    @CollectionTable(name = "news_post_images", joinColumns = @JoinColumn(name = "news_post_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
    private LocalDateTime pubDate;
}
