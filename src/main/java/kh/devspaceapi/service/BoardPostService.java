package kh.devspaceapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kh.devspaceapi.model.entity.BoardPost;

public interface BoardPostService {
	public void insert(BoardPost boardPost);

	// 게시판 출력(1개)
	public BoardPost select(BoardPost boardPost);

	// 게시판 수정
	public BoardPost update(BoardPost boardPost);

	// 게시판 삭제
	public void delete(BoardPost boardPost);

	// 게시판 출력(전체)
	public Page<BoardPost> selectAll(Pageable pageable);

	// 게시판 검색
	public Page<BoardPost> search(String searchType, String keyword, String category, Pageable pageable);

	// 카테고리 검색
	public Page<BoardPost> selectAllByCategory(String category, Pageable pageable);

}
