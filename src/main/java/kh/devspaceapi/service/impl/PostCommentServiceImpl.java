package kh.devspaceapi.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.PostViewLog;
import kh.devspaceapi.model.entity.Users;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.model.mapper.PostCommentMapper;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.repository.PostViewLogRepository;
import kh.devspaceapi.repository.UsersRepository;
import kh.devspaceapi.service.PostCommentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

	private final PostCommentRepository postCommentRepository;
	private final PostViewLogRepository postViewLogRepository;
	private final PostCommentMapper postCommentMapper;
	private final UsersRepository usersRepository;

	/**
	 * 댓글 생성 후, 댓글 카운트(+1) 로그 적재
	 */
	@Override
	@Transactional
	public PostCommentResponseDto create(Long targetId, TargetType targetType, String userId, String content) {
		Users userRef = usersRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));

		PostComment c = new PostComment();
		c.setTargetId(targetId);
		c.setTargetType(targetType);
		c.setUser(userRef); // 작성자 연결
		c.setContent(content);
		c.setActive(true);
		c.setCreatedAt(LocalDateTime.now());
		c.setUpdatedAt(LocalDateTime.now());

		PostComment saved = postCommentRepository.save(c);

		// 댓글 +1 집계 로그 (PostViewLog.userId 타입에 맞게 — 현재 Users로 가정)
		postViewLogRepository.save(PostViewLog.builder().targetId(targetId).targetType(targetType).userId(userRef)
				.viewDate(new Timestamp(System.currentTimeMillis())).viewCount(0).commentCount(1).build());

		return postCommentMapper.toDto(saved);
	}

	/** 단건 조회 */
	@Override
	public PostCommentResponseDto get(Long commentId) {
		PostComment c = postCommentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));
		return postCommentMapper.toDto(c);
	}

	/**
	 * 댓글 내용 수정 path의 게시글(targetId/type)에 해당 댓글의 속하는지 확인
	 */
	@Override
	@Transactional
	public PostCommentResponseDto update(Long targetId, TargetType targetType, Long commentId, String content,
			String userId) {
		PostComment c = postCommentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));

		if (!c.getTargetId().equals(targetId) || c.getTargetType() != targetType) {
			throw new IllegalArgumentException("해당 댓글은 지정된 게시글에 속하지 않습니다.");
		}

		if (!c.getUser().getUserId().equals(userId)) {
			throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
		}

		c.setContent(content);
		c.setUpdatedAt(LocalDateTime.now());
		return postCommentMapper.toDto(c);
	}

	/**
	 * 댓글 삭제(소프트 삭제) active=false로 전환 집계 로그에 댓글 카운트(-1) 기록
	 */
	@Override
	@Transactional
	public void delete(Long targetId, TargetType targetType, Long commentId, String userId) {
		PostComment c = postCommentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));

		if (!c.getTargetId().equals(targetId) || c.getTargetType() != targetType) {
			throw new IllegalArgumentException("해당 댓글은 지정된 게시글에 속하지 않습니다.");
		}

		if (!c.getUser().getUserId().equals(userId)) {
			throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
		}

		c.setActive(false); // 소프트 삭제
		c.setUpdatedAt(LocalDateTime.now());
	}

	/**
	 * 특정 대상의 노출(active=true) 댓글 목록 페이징 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PostCommentResponseDto> page(Long targetId, TargetType targetType, Pageable pageable) {
		return postCommentRepository.findPostsByTargetIdAndTargetTypeAndActiveTrue(targetId, targetType, pageable)
				.map(postCommentMapper::toDto);
	}
}
