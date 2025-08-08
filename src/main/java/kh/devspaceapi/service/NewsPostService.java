package kh.devspaceapi.service;

import kh.devspaceapi.comm.response.PageResponse;
import kh.devspaceapi.model.dto.newsPost.NewsPostRequestDto;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;

public interface NewsPostService {

	/**
	 * 지정된 뉴스 게시글 ID에 해당하는 뉴스 게시글 정보를 조회
	 *
	 * @param newsPostId 조회할 뉴스 게시글의 고유 ID
	 * @return NewsPostResponseDto 뉴스 게시글의 상세 정보를 담은 DTO 객체
	 */
	NewsPostResponseDto getNewsPostById(Long newsPostId);

	/**
	 * @param request 검색할 뉴스 게시글의 제목과 내용
	 * @return 뉴스 게시글 한 건을 표현하는 DTO(뉴스 게시글 목록을 페이지 단위로 묶음)
	 */
	PageResponse<NewsPostResponseDto> getNewsPost(NewsPostRequestDto request);

}
