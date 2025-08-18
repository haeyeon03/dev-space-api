package kh.devspaceapi.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.entity.PostViewLog;

public interface PostViewLogRepository extends JpaRepository<PostViewLog, Long> {

	@Query("SELECT FUNCTION('TO_CHAR', p.viewDate, 'YYYY-MM-DD') AS viewDate, COUNT(p) AS viewCount "
			+ "FROM PostViewLog p " + "WHERE p.viewDate BETWEEN :startDate AND :endDate "
			+ "GROUP BY FUNCTION('TO_CHAR', p.viewDate, 'YYYY-MM-DD') " + "ORDER BY viewDate ASC")
	List<Object[]> getDailyViewCountBetween(@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate);

	@Query("""
			SELECT 
			    CASE
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 10 AND 19 THEN '10대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 20 AND 29 THEN '20대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 30 AND 39 THEN '30대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 40 AND 49 THEN '40대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 50 AND 59 THEN '50대'
			        ELSE '60대 이상'
			    END AS ageGroup,
			    u.gender AS gender,
			    COUNT(p) AS viewCount
			FROM PostViewLog p
			JOIN p.userId u
			WHERE p.viewDate BETWEEN :startDate AND :endDate
			GROUP BY 
			    CASE
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 10 AND 19 THEN '10대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 20 AND 29 THEN '20대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 30 AND 39 THEN '30대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 40 AND 49 THEN '40대'
			        WHEN FLOOR(MONTHS_BETWEEN(CURRENT_DATE, u.birthdate) / 12) BETWEEN 50 AND 59 THEN '50대'
			        ELSE '60대 이상'
			    END,
			    u.gender
			ORDER BY ageGroup
			""")
	    List<Object[]> getAgeGenderStats(
	            @Param("startDate") LocalDateTime startDate,
	            @Param("endDate") LocalDateTime endDate
	    );

}
