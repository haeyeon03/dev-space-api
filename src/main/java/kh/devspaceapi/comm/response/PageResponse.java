package kh.devspaceapi.comm.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageResponse<T> {
	private List<T> contents;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean isFirst;
	private boolean isLast;

	public PageResponse(Page<T> data) {
		super();
		this.contents = data.getContent();
		this.pageNumber = data.getPageable().getPageNumber() + 1;
		this.pageSize = data.getPageable().getPageSize();
		this.totalElements = data.getTotalElements();
		this.totalPages = data.getTotalPages();
		this.isFirst = data.isFirst();
		this.isLast = data.isLast();
	}

}
