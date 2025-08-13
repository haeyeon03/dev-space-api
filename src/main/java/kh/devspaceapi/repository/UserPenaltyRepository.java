package kh.devspaceapi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.entity.UserPenalty;

public interface UserPenaltyRepository extends JpaRepository<UserPenalty, Integer>{
	
	//현재 정지중인 유저들 조회 쿼리
	@Query("""
	        SELECT up
	        FROM UserPenalty up
	        WHERE up.user.userId IN :userIds
	          AND up.effectiveAt IS NOT NULL
	          AND up.effectiveAt <= CURRENT_TIMESTAMP
	          AND (up.effectiveAt + FUNCTION('NUMTODSINTERVAL', up.duration_sec, 'SECOND')) > CURRENT_TIMESTAMP
	        """)
	    List<UserPenalty> findActivePenaltiesByUserIds(@Param("userIds") Collection<String> userIds);
	
	

}
