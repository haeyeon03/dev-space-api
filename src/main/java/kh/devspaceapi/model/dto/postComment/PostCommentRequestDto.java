package kh.devspaceapi.model.dto.postComment;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kh.devspaceapi.model.dto.base.BasePageRequestDto;
import kh.devspaceapi.model.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentRequestDto extends BasePageRequestDto {
	private String content;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private Long targetId; // FK (NewsPostId or BoardPostId)
}
