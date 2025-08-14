package kh.devspaceapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.enums.TargetType;

/** 
 * 댓글(PostComment) 조회용 리포지토리.
 * 
 * targetId + targetType 로 댓글의 "소속(게시글/뉴스/댓글)"을 구분.
 */
@Transactional
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

	/**
	 * 특정 대상(targetId, targetType)의 댓글 전체 목록을 commentId 내림차순으로 반환 (active 필터 없음).
	 * 관리자/내부용으로 “숨김 포함 전부” 볼 때 사용 가능.
	 * 정렬만, 페이징 없음(List 반환).
	 */
	List<PostComment> findByTargetIdAndTargetTypeOrderByPostCommentIdDesc(Long targetId, TargetType targetType);

	/** 특정 대상(targetId, targetType)의 댓글 전체 목록 (active 필터/정렬 없음). 
	 * 숨김 포함 전부 반환.
	 */
	List<PostComment> findByTargetIdAndTargetType(Long targetId, TargetType targetType);

	// 특정 대상의 “보이는(active=true)” 댓글을 페이징으로 조회.
	Page<PostComment> findPostsByTargetIdAndTargetTypeAndActiveTrue(Long targetId, TargetType targetType,
			Pageable pageable);

	// 특정 대상의 “보이는(active=true)” 댓글을 페이징으로 조회.
	Page<PostComment> findCommentsByTargetIdAndTargetTypeAndActiveTrue(Long newsPostId, TargetType news,
			Pageable pageable);
}
