package kh.devspaceapi.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.entity.PostViewLog;

public interface PostViewLogRepository extends JpaRepository<PostViewLog, Long> {

	//PostViewLog에 저장된 날짜별 조회수를 정렬한 뒤 가져오는 쿼리
	@Query("SELECT FUNCTION('TO_CHAR', p.viewDate, 'YYYY-MM-DD')"+
	"AS viewDate, COUNT(p) AS viewCount FROM PostViewLog p "+
	"GROUP BY FUNCTION('TO_CHAR',p.viewDate, 'YYYY-MM-DD')"+
	"ORDER BY viewDate ASC")
	List<Object[]> getDailyViewCountBetween(
			//기간별 조회를 할 수 있게해주는 between조건 파람
			@Param("startDate") Timestamp stertDate,
			@Param("endDate") Timestamp endDate
			);
	
	/*
	 * Users(gender,birthDate)와 PostViewLogs(viewDate)를 조인하여 나이대와 성별에 따라 CASE로 분류하여
	 * 데이터를 가져온다
	 */
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
			JOIN Users u ON p.userId.id = u.id
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
	List<Object[]> getAgeGenderDistribution(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

}
