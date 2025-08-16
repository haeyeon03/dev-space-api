package kh.devspaceapi.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import kh.devspaceapi.model.dto.admin.stats.ReportDetailDto;
import kh.devspaceapi.model.dto.admin.stats.ReportListDto;
import kh.devspaceapi.model.dto.admin.stats.UpdateReportStatusRequestDto;
import kh.devspaceapi.model.entity.BoardPost;
import kh.devspaceapi.model.entity.ContentReport;
import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.Users;
import kh.devspaceapi.model.entity.enums.ReportStatus;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.repository.ContentReportRepository;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.repository.UsersRepository;
import kh.devspaceapi.repository.spec.ContentReportSpecs;
import kh.devspaceapi.service.ReportService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
	
	@Autowired
	private ContentReportRepository contentReportRepository;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private NewsPostRepository newsPostRepository;
	@Autowired
	private BoardPostRepository boardPostRepository;
	@Autowired
	private PostCommentRepository postCommentRepository;
	
	@Override
    public Page<ReportListDto> getReports(ReportStatus status, TargetType type, String reporterLike, Pageable pageable) {
        // 기본값: 처리중
        if (status == null) status = ReportStatus.PROCESSING;

        Specification<ContentReport> spec = Specification
                .where(ContentReportSpecs.statusIs(status))
                .and(ContentReportSpecs.targetTypeIs(type))
                .and(ContentReportSpecs.reporterContains(reporterLike));

        Page<ContentReport> page = contentReportRepository.findAll(spec, pageable);

        // 대상 제목/링크 일괄 조회를 위한 group by type
        Map<TargetType, List<ContentReport>> byType = page.getContent().stream()
                .collect(Collectors.groupingBy(ContentReport::getTargetType));

        Map<Long, String> newsTitleMap = fetchNewsTitles(byType.get(TargetType.NEWS));
        Map<Long, String> boardTitleMap = fetchBoardTitles(byType.get(TargetType.BOARD));
        Map<Long, String> commentPreviewMap = fetchCommentPreview(byType.get(TargetType.COMMENT));

        List<ReportListDto> rows = page.getContent().stream().map(r -> {
            String title;
            String path;
            if (r.getTargetType() == TargetType.NEWS) {
                title = newsTitleMap.getOrDefault(r.getTargetId(), "(뉴스 없음)");
                path  = "/news/" + r.getTargetId();
            } else if (r.getTargetType() == TargetType.BOARD) {
                title = boardTitleMap.getOrDefault(r.getTargetId(), "(게시글 없음)");
                path  = "/board/" + r.getTargetId();
            } else { // COMMENT
                title = commentPreviewMap.getOrDefault(r.getTargetId(), "(댓글 없음)");
                // 댓글이 어떤 게시물에 속하는지 PostComment에서 찾아 프론트 규칙대로 링크
                Long postId = findParentPostId(r.getTargetId());
                // 여기서는 뉴스/게시판 구분이 추가로 필요하다면 PostComment의 targetType으로 분기
                path  = "/comment/" + r.getTargetId() + (postId != null ? ("?postId=" + postId) : "");
            }

            return ReportListDto.builder()
                    .reportId(r.getReportId())
                    .targetType(r.getTargetType())
                    .targetId(r.getTargetId())
                    .contentTitle(title)
                    .reason(r.getReason())
                    .reporterUserId(r.getReporter() != null ? r.getReporter().getUserId() : null)
                    .status(r.getStatus())
                    .reportedAt(r.getCreatedAt()) // BaseEntity.createdAt 가정
                    .contentPath(path)
                    .build();
        }).toList();

        return new PageImpl<>(rows, pageable, page.getTotalElements());
    }

    @Override
    public ReportDetailDto getReportDetail(Long reportId) {
        ContentReport r = contentReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다: " + reportId));

        String title;
        String bodyPreview = null;
        String path;

        if (r.getTargetType() == TargetType.NEWS) {
            title = newsPostRepository.findById(r.getTargetId()).map(NewsPost::getTitle).orElse("(뉴스 없음)");
            path  = "/news/" + r.getTargetId();
        } else if (r.getTargetType() == TargetType.BOARD) {
            title = boardPostRepository.findById(r.getTargetId()).map(BoardPost::getTitle).orElse("(게시글 없음)");
            path  = "/board/" + r.getTargetId();
        } else {
            // 댓글
            PostComment c = postCommentRepository.findById(r.getTargetId()).orElse(null);
            title = (c != null ? trimText(c.getContent()) : "(댓글 없음)");
            bodyPreview = (c != null ? trimText(c.getContent(), 200) : null);
            path  = "/comment/" + r.getTargetId();
        }

        return ReportDetailDto.builder()
                .reportId(r.getReportId())
                .targetType(r.getTargetType())
                .targetId(r.getTargetId())
                .contentTitle(title)
                .contentBodyPreview(bodyPreview)
                .reason(r.getReason())
                .reporterUserId(r.getReporter() != null ? r.getReporter().getUserId() : null)
                .status(r.getStatus())
                .reportedAt(r.getCreatedAt())
                .handlerUserId(r.getHandler() != null ? r.getHandler().getUserId() : null)
                .handlerNote(r.getHandlerNote())
                .processedAt(r.getProcessedAt())
                .contentPath(path)
                .build();
    }

    @Override
    public ReportDetailDto updateReportStatus(Long reportId, String handlerUserId, UpdateReportStatusRequestDto req) {
        ContentReport r = contentReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다: " + reportId));

        if (req.getStatus() == null) throw new IllegalArgumentException("status는 필수입니다.");

        r.setStatus(req.getStatus());
        r.setHandlerNote(req.getHandlerNote());

        if (handlerUserId != null) {
            Users handler = usersRepository.findById(handlerUserId)
                    .orElseThrow(() -> new IllegalArgumentException("관리자 계정을 찾을 수 없습니다: " + handlerUserId));
            r.setHandler(handler);
        }

        if (req.getStatus() == ReportStatus.COMPLETED) {
            r.setProcessedAt(LocalDateTime.now());
        } else {
            r.setProcessedAt(null);
        }

        contentReportRepository.save(r);
        // 변경 후 상세 반환
        return getReportDetail(reportId);
    }

    /* ================== 내부 유틸 ================== */

    private Map<Long, String> fetchNewsTitles(List<ContentReport> list) {
        if (list == null || list.isEmpty()) return Map.of();
        List<Long> ids = list.stream().map(ContentReport::getTargetId).distinct().toList();
        return newsPostRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(NewsPost::getNewsPostId, NewsPost::getTitle)); // 엔티티 필드명 맞게 수정
    }

    private Map<Long, String> fetchBoardTitles(List<ContentReport> list) {
        if (list == null || list.isEmpty()) return Map.of();
        List<Long> ids = list.stream().map(ContentReport::getTargetId).distinct().toList();
        return boardPostRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(BoardPost::getBoardPostId, BoardPost::getTitle)); // 필드명 맞게 수정
    }

    private Map<Long, String> fetchCommentPreview(List<ContentReport> list) {
        if (list == null || list.isEmpty()) return Map.of();
        List<Long> ids = list.stream().map(ContentReport::getTargetId).distinct().toList();
        return postCommentRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(PostComment::getPostCommentId, c -> trimText(c.getContent())));
    }

    private String trimText(String s) { return trimText(s, 60); }
    private String trimText(String s, int max) {
        if (s == null) return null;
        String t = s.replaceAll("\\s+", " ").trim();
        return t.length() > max ? t.substring(0, max) + "…" : t;
    }

    private Long findParentPostId(Long commentId) {
        return postCommentRepository.findById(commentId)
                .map(PostComment::getTargetId) // 실제 댓글 엔티티 구조에 맞게 수정
                .orElse(null);
    }

}
