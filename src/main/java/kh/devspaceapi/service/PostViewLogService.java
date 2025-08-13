package kh.devspaceapi.service;

import java.time.LocalDate;
import java.util.List;

import kh.devspaceapi.model.dto.admin.stats.AgeGenderDistributionResponseDto;
import kh.devspaceapi.model.dto.admin.stats.DailyViewCountResponseDto;

public interface PostViewLogService {
	
	//일별 조회수를 List타입으로 return service
	List<DailyViewCountResponseDto> getDailyViewCountBetween(LocalDate startDate, LocalDate endDate);
	
	//연령별 성별별 분포도를 List타입으로 return service
	List<AgeGenderDistributionResponseDto> getAgeGenderDistribution(LocalDate startDate, LocalDate endDate);

}
