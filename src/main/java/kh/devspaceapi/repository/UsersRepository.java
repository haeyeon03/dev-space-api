package kh.devspaceapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {

	// 관리자 남여 성비카운트
	@Query("SELECT u.gender, COUNT(u) " + "FROM Users u " + "GROUP BY u.gender")
	List<Object[]> countUserByGender();

	// 관리자 유저검색
	@Query("""
			SELECT u
			FROM Users u
			WHERE (:searchType IS NULL OR :keyword IS NULL OR
			      (LOWER(:searchType) = 'nickname' AND LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
			      OR (LOWER(:searchType) = 'userId' AND LOWER(u.userId) LIKE LOWER(CONCAT('%', :keyword, '%')))
			)
			AND (:role IS NULL OR LOWER(:role) IN ('admin','user') AND LOWER(u.role) = LOWER(:role))
			""")
	Page<Users> searchUsers(@Param("searchType") String searchType, @Param("keyword") String keyword, @Param("role") String role, Pageable pageable);

	Optional<Users> findByUserId(String userId);
}
