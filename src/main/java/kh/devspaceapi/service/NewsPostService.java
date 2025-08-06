package kh.devspaceapi.service;

import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;

public interface NewsPostService {

    /**
     * 지정된 뉴스 게시글 ID에 해당하는 뉴스 게시글 정보를 조회
     *
     * @param newsPostId 조회할 뉴스 게시글의 고유 ID
     * @return NewsPostResponseDto 뉴스 게시글의 상세 정보를 담은 DTO 객체
     */
    NewsPostResponseDto getNewsPost(Long newsPostId);
}
