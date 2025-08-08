package kh.devspaceapi.repository;

import java.sql.Timestamp;
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

}
