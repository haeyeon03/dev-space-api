package kh.devspaceapi.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BOARD_POST")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "board_post_seq_gen", sequenceName = "BOARD_POST_SEQ", allocationSize = 1)
public class BoardPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_post_seq_gen")
    private Long boardPostId;
    private String title;
    private String category;
    @Lob // DB Table 필드의 타입
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private Users user;
    private int viewCount;
    private int commentCount;
}
