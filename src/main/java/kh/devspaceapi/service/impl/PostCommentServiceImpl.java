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
import kh.devspaceapi.service.PostCommentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostViewLogRepository postViewLogRepository;
    private final PostCommentMapper postCommentMapper;

    @Override
    @Transactional
    public PostCommentResponseDto create(Long targetId, TargetType targetType, String userId, String content) {
        // Users 참조는 PK만 세팅(프록시 없이도 JPA가 FK로 저장 가능)
        Users userRef = null;
        if (userId != null && !userId.isBlank()) {
            userRef = new Users();
            userRef.setUserId(userId);
        }

        PostComment c = new PostComment();
        c.setTargetId(targetId);
        c.setTargetType(targetType);
        c.setUser(userRef);
        c.setContent(content);
        // BaseEntity
        c.setActive(false);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());

        PostComment saved = postCommentRepository.save(c);

        // 댓글 +1
        postViewLogRepository.save(PostViewLog.builder()
            .targetId(targetId)
            .targetType(targetType)
            .userId(userRef)
            .viewDate(new Timestamp(System.currentTimeMillis()))
            .viewCount(0)
            .commentCount(1)
            .build());

        return postCommentMapper.toDto(saved);
    }

    @Override
    public PostCommentResponseDto get(Long commentId) {
        PostComment c = postCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));
        return postCommentMapper.toDto(c);
    }

    @Override
    @Transactional
    public PostCommentResponseDto update(Long commentId, String content) {
        PostComment c = postCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));
        c.setContent(content);
        c.setUpdatedAt(LocalDateTime.now());
        return postCommentMapper.toDto(c);
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        PostComment c = postCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));

        // 삭제
        c.setActive(true);
        c.setUpdatedAt(LocalDateTime.now());

        // 댓글 -1
        postViewLogRepository.save(PostViewLog.builder()
            .targetId(c.getTargetId())
            .targetType(c.getTargetType())
            .userId(c.getUser())
            .viewDate(new Timestamp(System.currentTimeMillis()))
            .viewCount(0)
            .commentCount(-1)
            .build());
    }

    @Override
    public Page<PostCommentResponseDto> page(Long targetId, TargetType targetType, Pageable pageable) {
        return postCommentRepository
            .findByTargetIdAndTargetTypeAndActiveFalse(targetId, targetType, pageable)
            .map(postCommentMapper::toDto);
    }
}
