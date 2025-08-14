package kh.devspaceapi.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kh.devspaceapi.model.dto.boardPost.BoardPostResponseDto;
import kh.devspaceapi.model.entity.BoardPost;
import kh.devspaceapi.model.entity.PostViewLog;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.repository.PostViewLogRepository;
import kh.devspaceapi.service.BoardPostService;
import lombok.RequiredArgsConstructor;

/**
 * 자유게시판 서비스 구현.
 *
 * 보이는 글: active=true
 * 숨김(삭제) 처리: active=false (소프트 삭제)
 * 상세 조회 시(Post 상세) 조회수는 PostViewLog 테이블에 1줄 적재(viewCount=1)
 * 댓글 수 증감도 PostViewLog에 합산 방식으로 관리(commentCount 누적)
 * 목록/검색은 Repository에서 active=true만 조회
 */
@Service
@RequiredArgsConstructor
public class BoardPostServiceImpl implements BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final PostViewLogRepository postViewLogRepository;

    @PersistenceContext
    private EntityManager em; // PostViewLog 합계 집계용

    /**
     * 게시글 생성.
     * 생성/수정 시각 세팅 후 저장
     * BaseEntity의 @PrePersist도 동작(활성화 기본 true 등)
     */
    @Override
    @Transactional
    public void insert(BoardPost boardPost) {
        LocalDateTime now = LocalDateTime.now();
        boardPost.setCreatedAt(now);
        boardPost.setUpdatedAt(now);
        boardPostRepository.save(boardPost);
    }

    /**
     * 게시글 단건 조회(소유권/활성여부 체크는 정책에 따라 컨트롤러/서비스에서 별도 처리 가능).
     * 존재 여부만 체크하여 반환
     */
    @Override
    @Transactional
    public BoardPost select(BoardPost boardPost) {
        BoardPost selectedPost = boardPostRepository.findByBoardPostId(boardPost.getBoardPostId())
            .orElseThrow(() -> new IllegalArgumentException("게시글 찾을 수 없음 " + boardPost.getBoardPostId()));
        return selectedPost;
    }

    /**
     * 게시글 수정.
     * 제목/내용/카테고리 변경 + updatedAt 갱신
     */
    @Override
    @Transactional
    public BoardPost update(BoardPost boardPost) {
        BoardPost updatedPost = boardPostRepository.findByBoardPostId(boardPost.getBoardPostId())
            .orElseThrow(() -> new IllegalArgumentException("게시글 찾을 수 없음 " + boardPost.getBoardPostId()));
        updatedPost.setTitle(boardPost.getTitle());
        updatedPost.setContent(boardPost.getContent());
        updatedPost.setCategory(boardPost.getCategory());
        updatedPost.setUpdatedAt(LocalDateTime.now());
        return updatedPost;
    }

    /**
     * 게시글 삭제(소프트 삭제).
     * active=false 로 전환, updatedAt 갱신
     */
    @Override
    @Transactional
    public void delete(BoardPost boardPost) {
        BoardPost deletedPost = boardPostRepository.findById(boardPost.getBoardPostId())
            .orElseThrow(() -> new IllegalArgumentException("게시글 찾을 수 없음 " + boardPost.getBoardPostId()));
        deletedPost.setActive(false); // 화면 비노출
        deletedPost.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 게시글 목록(보이는 글만).
     * 정렬/페이징은 Pageable에 위임
     */
    @Override
    public Page<BoardPost> selectAll(Pageable pageable) {
        return boardPostRepository.findByActiveTrue(pageable);
    }

    /**
     * 카테고리별 목록(보이는 글만).
     */
    @Override
    public Page<BoardPost> selectAllByCategory(String category, Pageable pageable) {
        return boardPostRepository.findByActiveTrueAndCategory(category, pageable);
    }

    /**
     * 검색(보이는 글만).
     * type/title/nickname/title+content 조합 처리
     * 카테고리 필터 유/무에 따라 분기
     */
    @Override
    public Page<BoardPost> search(String searchType, String keyword, String category, Pageable pageable) {
        String type = (searchType == null) ? "" : searchType.trim().toLowerCase();
        String kw = (keyword == null) ? "" : keyword.trim();
        boolean hasKw = !kw.isEmpty();
        boolean hasCat = category != null && !category.trim().isEmpty();

        if (!hasKw) {
            return hasCat
                ? boardPostRepository.findByActiveTrueAndCategory(category, pageable)
                : boardPostRepository.findByActiveTrue(pageable);
        }

        if (hasCat) {
            if ("title".equals(type)) {
                return boardPostRepository.findByActiveTrueAndCategoryAndTitleContaining(category, kw, pageable);
            } else if ("nickname".equals(type)) {
                return boardPostRepository.findByActiveTrueAndCategoryAndUser_NicknameContaining(category, kw, pageable);
            } else if ("titlecontent".equals(type) || "title+content".equals(type)) {
                return boardPostRepository.findByActiveTrueAndCategoryAndTitleContainingOrActiveTrueAndCategoryAndContentContaining(
                    category, kw, category, kw, pageable);
            } else {
                return boardPostRepository.findByActiveTrueAndCategory(category, pageable);
            }
        } else {
            if ("title".equals(type)) {
                return boardPostRepository.findByActiveTrueAndTitleContaining(kw, pageable);
            } else if ("nickname".equals(type)) {
                return boardPostRepository.findByActiveTrueAndUser_NicknameContaining(kw, pageable);
            } else if ("titlecontent".equals(type) || "title+content".equals(type)) {
                return boardPostRepository.findByActiveTrueAndTitleContainingOrActiveTrueAndContentContaining(kw, kw, pageable);
            } else {
                return boardPostRepository.findByActiveTrue(pageable);
            }
        }
    }

    /**
     * 게시글 상세 조회(+조회수 1 적재).
     * PostViewLog에 한 줄 insert(viewCount=1) -> 합계는 쿼리로 계산
     * 합계 조회 결과를 DTO로 반환
     */
    @Override
    @Transactional
    public BoardPostResponseDto getPost(Long postId) {
        BoardPost post = boardPostRepository.findByBoardPostId(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글 찾을 수 없음 " + postId));

        // 1) 조회 로그 저장 (userId는 익명/비회원이면 null 유지)
        postViewLogRepository.save(
            PostViewLog.builder()
                .targetId(post.getBoardPostId())
                .targetType(TargetType.BOARD)
                .userId(null)
                .viewDate(new Timestamp(System.currentTimeMillis()))
                .viewCount(1)       // 조회수 +1 의미
                .commentCount(0)    // 댓글 카운트 변화 없음
                .build()
        );

        // 2) 합계 계산
        int views = getViewCountOf(post.getBoardPostId());
        int comments = getCommentCountOf(post.getBoardPostId());

        // 3) DTO 반환
        return BoardPostResponseDto.builder()
            .boardPostId(post.getBoardPostId())
            .title(post.getTitle())
            .category(post.getCategory())
            .content(post.getContent())
            .userNickname(post.getUser() != null ? post.getUser().getNickname() : null)
            .createdAt(post.getCreatedAt())
            .viewCount(views)
            .commentCount(comments)
            .build();
    }

    /**
     * 댓글 수 +1 기록.
     * 실제 댓글 저장/삭제는 PostCommentService에서 수행
     * 여기서는 합계 집계를 위해 로그만 쌓는 간단 API(필요 시 사용)
     */
    @Override
    @Transactional
    public void plusCommentCount(Long postId) {
        postViewLogRepository.save(
            PostViewLog.builder()
                .targetId(postId)
                .targetType(TargetType.BOARD)
                .userId(null)
                .viewDate(new Timestamp(System.currentTimeMillis()))
                .viewCount(0)
                .commentCount(1) // 댓글수 +1
                .build()
        );
    }

    /**
     * 조회수 합계 조회(PostViewLog 기반).
     */
    @Override
    public int getViewCountOf(Long postId) {
        Long sum = em.createQuery(
                "select coalesce(sum(p.viewCount),0) from PostViewLog p " +
                "where p.targetId = :id and p.targetType = :type", Long.class)
            .setParameter("id", postId)
            .setParameter("type", TargetType.BOARD)
            .getSingleResult();
        return sum.intValue();
    }

    /**
     * 댓글 수 합계 조회(PostViewLog 기반).
     * 댓글 작성 시 +1, 삭제 시 -1 로 누적되어 합계가 계산됨.
     */
    @Override
    public int getCommentCountOf(Long postId) {
        Long sum = em.createQuery(
                "select coalesce(sum(p.commentCount),0) from PostViewLog p " +
                "where p.targetId = :id and p.targetType = :type", Long.class)
            .setParameter("id", postId)
            .setParameter("type", TargetType.BOARD)
            .getSingleResult();
        return sum.intValue();
    }
}
