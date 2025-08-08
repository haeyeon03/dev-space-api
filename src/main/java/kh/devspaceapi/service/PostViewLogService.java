package kh.devspaceapi.service;

import java.sql.Timestamp;
import java.util.List;

import kh.devspaceapi.model.dto.admin.stats.DailyViewCountResponseDto;

public interface PostViewLogService {
	
	//일별 조회수를 List타입으로 return service
	List<DailyViewCountResponseDto> getDailyViewCountBetween(Timestamp startDate, Timestamp endDate);
	

}
