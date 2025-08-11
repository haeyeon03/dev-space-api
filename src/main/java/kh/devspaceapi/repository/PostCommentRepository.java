package kh.devspaceapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.enums.TargetType;

@Transactional
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
	List<PostComment> findByTargetIdAndTargetTypeOrderByPostCommentIdDesc(Long targetId, TargetType targetType);

	List<PostComment> findByTargetIdAndTargetType(Long targetId, TargetType targetType);

}
