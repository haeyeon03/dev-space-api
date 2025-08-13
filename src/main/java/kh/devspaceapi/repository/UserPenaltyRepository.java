package kh.devspaceapi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.entity.UserPenalty;

public interface UserPenaltyRepository extends JpaRepository<UserPenalty, Integer>{

	@Query(value = """
		    SELECT *
		    FROM USER_PENALTY up
		    WHERE up.USER_ID IN (:userIds)
		      AND up.EFFECTIVE_AT IS NOT NULL
		      AND up.EFFECTIVE_AT <= SYSTIMESTAMP
		      AND (up.EFFECTIVE_AT + NUMTODSINTERVAL(up.DURATION_SEC, 'SECOND')) > SYSTIMESTAMP
		""", nativeQuery = true)
	    List<UserPenalty> findActivePenaltiesByUserIds(@Param("userIds") Collection<String> userIds);

}
