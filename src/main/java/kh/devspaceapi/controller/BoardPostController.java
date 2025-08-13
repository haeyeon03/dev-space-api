package kh.devspaceapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private final BoardPostService boardPostService;
    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody BoardPost body) throws Exception {
        boardPostService.insert(body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardPostResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(boardPostService.getPost(id));
    }

    @GetMapping
    public Page<BoardPostResponseDto> page(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "recent") String order,
                                           @RequestParam(required = false) String category) {

        Sort sort;
        String key = order == null ? "recent" : order.trim().toLowerCase();
        if ("oldest".equals(key)) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BoardPost> result = (category != null && !category.trim().isEmpty())
                ? boardPostService.selectAllByCategory(category, pageable)
                : boardPostService.selectAll(pageable);

        return result.map(this::toDto);
    }

    @PutMapping("/{id}")
    public BoardPostResponseDto update(@PathVariable Long id, @RequestBody BoardPost body) {
        body.setBoardPostId(id);
        BoardPost updated = boardPostService.update(body);
        return toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        BoardPost probe = new BoardPost();
        probe.setBoardPostId(id);
        boardPostService.delete(probe);
        return ResponseEntity.noContent().build();
    }

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
                .viewCount(views)
                .commentCount(comments)
                .build();
    }
    
    
 // 댓글
    @GetMapping("/{boardPostId}/comments")
    public Page<PostCommentResponseDto> listComments(@PathVariable Long boardPostId,
                                                     @RequestParam int curPage,
                                                     @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(
            Math.max(curPage, 0),
            Math.max(pageSize, 1),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return postCommentService.page(boardPostId, TargetType.BOARD, pageable);
    }

    @PostMapping("/{boardPostId}/comments")
    public ResponseEntity<PostCommentResponseDto> createComment(@PathVariable Long boardPostId,
                                                                @RequestBody CommentContent req) {
        PostCommentResponseDto dto = postCommentService.create(
            boardPostId, TargetType.BOARD, null, req.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{boardPostId}/comments/{commentId}")
    public PostCommentResponseDto updateComment(@PathVariable Long boardPostId,
                                                @PathVariable Long commentId,
                                                @RequestBody CommentContent req) {
        return postCommentService.update(commentId, req.content());
    }

    @DeleteMapping("/{boardPostId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long boardPostId,
                                              @PathVariable Long commentId) {
        postCommentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
    
    public record CommentContent(String content) {}

}
