package kh.devspaceapi.service;

import org.springframework.data.domain.Page;

import kh.devspaceapi.comm.response.PageResponse;
import kh.devspaceapi.model.dto.newsPost.NewsPostRequestDto;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.model.dto.postComment.PostCommentRequestDto;
import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;

/**
 * 뉴스 게시글 관련 서비스 인터페이스
 * - 뉴스 게시글 조회, 단건 조회, 삭제
 * - 뉴스 게시글에 대한 댓글 조회
 */
public interface NewsPostService {

    /**
     * 뉴스 게시글 목록 조회
     * 검색 조건(제목, 내용 등)과 페이지 정보를 기준으로 뉴스 게시글을 페이징하여 조회
     *
     * @param request NewsPostRequestDto : 검색 조건과 페이지 정보가 담긴 DTO
     * @return PageResponse<NewsPostResponseDto> : 페이징된 뉴스 게시글 리스트
     */
    PageResponse<NewsPostResponseDto> getNewsPost(NewsPostRequestDto request);

    /**
     * 단건 뉴스 게시글 조회
     *
     * @param newsPostId 조회할 뉴스 게시글 ID
     * @return NewsPostResponseDto : 조회된 뉴스 게시글 상세 정보 DTO
     */
    NewsPostResponseDto getNewsPostById(Long newsPostId);

    /**
     * 뉴스 게시글 삭제
     *
     * @param newsPostId 삭제할 뉴스 게시글 ID
     * @return Long 삭제된 뉴스 게시글 ID
     */
    public Long deleteNewsPost(Long newsPostId);

    /**
     * 뉴스 게시글의 댓글 목록 조회
     * 
     * @param newsPostId 댓글이 속한 뉴스 게시글 ID
     * @param request PostCommentRequestDto : 페이지 정보 등 댓글 조회 조건
     * @return Page<PostCommentResponseDto> : 페이징된 댓글 리스트
     */
    Page<PostCommentResponseDto> getCommentsByNewsPostId(Long newsPostId, PostCommentRequestDto request);

}
