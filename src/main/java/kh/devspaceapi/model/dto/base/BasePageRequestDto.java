package kh.devspaceapi.model.dto.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasePageRequestDto {

	private int curPage;
	private int pageSize;
}
