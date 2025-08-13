package kh.devspaceapi.model.dto.postComment;

import kh.devspaceapi.model.dto.base.BasePageRequestDto;
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
}
