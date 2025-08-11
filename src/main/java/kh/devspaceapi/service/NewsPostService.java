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
	 * 뉴스 게시글 검색 조건에 따라 페이지 단위로 뉴스 게시글 목록을 조회
	 * 
	 * @param request 검색 조건(제목, 내용, 페이지 정보 등)을 담은 DTO
	 * @return 뉴스 게시글 목록을 페이지 단위로 묶은 DTO
	 */
	PageResponse<NewsPostResponseDto> getNewsPost(NewsPostRequestDto request);

	/**
	 * 지정된 뉴스 게시글 ID에 해당하는 뉴스 게시글을 삭제
	 *
	 * @param newsPostId 삭제할 뉴스 게시글의 고유 ID
	 */
	public Long deleteNewsPost(Long newsPostId);

}
