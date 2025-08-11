package kh.devspaceapi.model.dto.newsPost;

import kh.devspaceapi.model.dto.base.BasePageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsPostRequestDto extends BasePageRequestDto {

	// BasePageRequestDto에서 curPage, pageSize 사용 
	private String title;
	private String content;

}
