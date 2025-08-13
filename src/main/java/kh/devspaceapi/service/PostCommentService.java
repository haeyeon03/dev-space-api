package kh.devspaceapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.entity.enums.TargetType;

public interface PostCommentService {

	public PostCommentResponseDto create(Long targetId, TargetType targetType, String userId, String content);

	public PostCommentResponseDto get(Long commentId);

	public PostCommentResponseDto update(Long targetId, TargetType targetType, Long commentId, String content);

    public void delete(Long targetId, TargetType targetType, Long commentId);

	public Page<PostCommentResponseDto> page(Long targetId, TargetType targetType, Pageable pageable);

}
