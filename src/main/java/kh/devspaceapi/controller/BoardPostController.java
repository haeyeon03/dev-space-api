package kh.devspaceapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kh.devspaceapi.model.dto.boardPost.BoardPostResponseDto;
import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.entity.BoardPost;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.service.BoardPostService;
import kh.devspaceapi.service.PostCommentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/board-posts")
@RequiredArgsConstructor
public class BoardPostController {

    // 서비스 레이어 주입
    private final BoardPostService boardPostService;
    private final PostCommentService postCommentService;

    /**
     * 게시글 생성
     * - Body로 title, category, content, (선택) user.userId 전달
     * - 201 Created 반환 (바디는 없음)
     */
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody BoardPost body) throws Exception {
        boardPostService.insert(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 게시글 상세 조회
     * - 조회수 로그 1건 적재 + 합계(viewCount/commentCount) 계산하여 DTO로 반환
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardPostResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(boardPostService.getPost(id));
    }

    /**
     * 게시글 목록 조회 (페이지네이션 + 정렬 + 카테고리 필터)
     * - /api/board-posts?page=0&size=10&order=recent|oldest&category=FREE
     * - active=true 인 글만 노출됨 (서비스/리포지토리에서 처리)
     */
    @GetMapping
    public Page<BoardPostResponseDto> page(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "recent") String order,
                                           @RequestParam(required = false) String category) {

        // 정렬 설정
        Sort sort;
        String key = order == null ? "recent" : order.trim().toLowerCase();
        if ("oldest".equals(key)) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");   // 오래된 순
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");  // 최신 순(기본)
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        // 카테고리 필터 유무에 따른 분기
        Page<BoardPost> result = (category != null && !category.trim().isEmpty())
                ? boardPostService.selectAllByCategory(category, pageable)
                : boardPostService.selectAll(pageable);

        // 엔티티 -> DTO 변환 (viewCount/commentCount는 서비스에서 합계 조회)
        return result.map(this::toDto);
    }

    /**
     * 게시글 수정
     * - path의 id를 엔티티에 주입 후 업데이트
     * - 수정 결과를 DTO로 반환
     */
    @PutMapping("/{id}")
    public BoardPostResponseDto update(@PathVariable Long id, @RequestBody BoardPost body) {
        body.setBoardPostId(id);
        BoardPost updated = boardPostService.update(body);
        return toDto(updated);
    }

    /**
     * 게시글 삭제(소프트 삭제)
     * - active=false 로 변경하여 목록에서 숨김 처리
     * - 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        BoardPost probe = new BoardPost();
        probe.setBoardPostId(id);
        boardPostService.delete(probe);
        return ResponseEntity.noContent().build();
    }

    /**
     * 게시글 검색
     * - searchType: title | nickname | title+content
     * - keyword: 검색어, category: 카테고리 필터
     * - 페이지네이션/정렬 동일
     */
    @GetMapping("/search")
    public Page<BoardPostResponseDto> search(@RequestParam(required = false) String searchType,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String category,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "recent") String order) {

        Sort sort;
        String key = order == null ? "recent" : order.trim().toLowerCase();
        if ("oldest".equals(key)) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return boardPostService.search(searchType, keyword, category, pageable)
                .map(this::toDto);
    }

    /**
     * 엔티티 -> DTO 변환 헬퍼
     * - viewCount / commentCount 는 엔티티 필드가 아니라, PostViewLog 합계로 계산한 값
     */
    private BoardPostResponseDto toDto(BoardPost e) {
        int views = boardPostService.getViewCountOf(e.getBoardPostId());
        int comments = boardPostService.getCommentCountOf(e.getBoardPostId());

        return BoardPostResponseDto.builder()
                .boardPostId(e.getBoardPostId())
                .title(e.getTitle())
                .category(e.getCategory())
                .content(e.getContent())
                .userNickname(e.getUser() != null ? e.getUser().getNickname() : null)
                .createdAt(e.getCreatedAt())
                .viewCount(views)           // 조회수 합계
                .commentCount(comments)     // 댓글수 합계
                .build();
    }

    // ===========================
    //           댓글 API
    // ===========================

    /**
     * 댓글 목록 (페이지네이션)
     * - 기본값: curPage=0, pageSize=10
     * - 최신 순(createdAt DESC)
     * - active=true 인 댓글만 노출(서비스/리포지토리)
     * - 예: /api/board-posts/1/comments?curPage=0&pageSize=10
     */
    @GetMapping("/{boardPostId}/comments")
    public Page<PostCommentResponseDto> listComments(@PathVariable Long boardPostId,
                                                     @RequestParam(defaultValue = "0") int curPage,
                                                     @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(
                Math.max(curPage, 0),
                Math.max(pageSize, 1),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return postCommentService.page(boardPostId, TargetType.BOARD, pageable);
    }

    /**
     * 댓글 작성
     * - Body: { "content": "내용" }
     * - userId는 지금 null로 넣어 테스트(인증 붙이면 SecurityContext에서 가져와 세팅 가능)
     * - 생성 시 active=true(보임)로 저장되도록 BaseEntity(@PrePersist) 사용 권장
     */
    @PostMapping("/{boardPostId}/comments")
    public ResponseEntity<PostCommentResponseDto> createComment(@PathVariable Long boardPostId,
                                                                @RequestBody CommentContent req) {
        PostCommentResponseDto dto = postCommentService.create(
                boardPostId, TargetType.BOARD, null, req.content()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * 댓글 수정
     * - URL에 boardPostId, commentId 모두 필요
     * - 서비스 시그니처: update(targetId, targetType, commentId, content)
     */
    @PutMapping("/{boardPostId}/comments/{commentId}")
    public PostCommentResponseDto updateComment(@PathVariable Long boardPostId,
                                                @PathVariable Long commentId,
                                                @RequestBody CommentContent req) {
        return postCommentService.update(boardPostId, TargetType.BOARD, commentId, req.content());
    }

    /**
     * 댓글 삭제(소프트 삭제)
     * - active=false 로 변경(목록/조회에서 숨김)
     * - 204 No Content
     */
    @DeleteMapping("/{boardPostId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long boardPostId,
                                              @PathVariable Long commentId) {
        postCommentService.delete(boardPostId, TargetType.BOARD, commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 요청 바디(JSON)용 DTO
     * - {"content":"내용"} 형태로 받기 위함
     */
    public record CommentContent(String content) {}
}
