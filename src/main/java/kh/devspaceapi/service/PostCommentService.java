package kh.devspaceapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.entity.enums.TargetType;

/**
 * 댓글(PostComment) 비즈니스 로직 인터페이스
 *
 * targetId + targetType 으로 댓글의 소속(게시글/뉴스/댓글)을 구분
 * 사용자 화면 기본 정책: active=true 인 댓글만 노출(리포지토리/서비스에서 필터)
 * 삭제는 소프트 삭제(일반적으로 active=false)로 처리
 */
public interface PostCommentService {

    /**
     * 댓글 생성.
     * @param targetId   대상 PK (예: 게시글 ID, 뉴스 ID)
     * @param targetType 대상 타입 (BOARD, NEWS, COMMENT)
     * @param userId     작성자 ID
     * @param content    댓글 내용
     * @return 생성된 댓글의 응답 DTO
     */
    public PostCommentResponseDto create(Long targetId, TargetType targetType, Long userId, String content);

    /**
     * 댓글 단건 조회 (ID 기준).
     * @param commentId 댓글 PK
     * @return 댓글 응답 DTO
     * @throws IllegalArgumentException 댓글이 없으면 예외
     */
    public PostCommentResponseDto get(Long commentId);

    /**
     * 댓글 수정.
     * @param targetId   대상 PK (검증용)
     * @param targetType 대상 타입 (검증용)
     * @param commentId  수정할 댓글 PK
     * @param content    수정할 내용
     * @return 수정된 댓글의 응답 DTO
     * @throws IllegalArgumentException 대상/타입 불일치 또는 댓글 없음
     */
    public PostCommentResponseDto update(Long targetId, TargetType targetType, Long commentId, String content);

    /**
     * 댓글 삭제(소프트 삭제 권장: active=false).
     * @param targetId   대상 PK (검증용)
     * @param targetType 대상 타입 (검증용)
     * @param commentId  삭제할 댓글 PK
     * @throws IllegalArgumentException 대상/타입 불일치 또는 댓글 없음
     */
    public void delete(Long targetId, TargetType targetType, Long commentId);

    /**
     * 댓글 페이지 조회 (active=true 만 노출).
     * - 정렬/페이지 크기는 Pageable에서 지정 (예: createdAt DESC).
     * @param targetId   대상 PK
     * @param targetType 대상 타입
     * @param pageable   페이징/정렬 정보
     * @return 댓글 페이지 DTO (active=true만)
     */
    public Page<PostCommentResponseDto> page(Long targetId, TargetType targetType, Pageable pageable);

}
