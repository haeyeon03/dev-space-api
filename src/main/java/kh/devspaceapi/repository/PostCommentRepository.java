package kh.devspaceapi.repository;

import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
	List<PostComment> findByTargetIdAndTargetTypeOrderByPostCommentIdDesc(Long targetId, TargetType targetType);

}
