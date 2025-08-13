package kh.devspaceapi.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import kh.devspaceapi.model.dto.admin.stats.AgeGenderDistributionResponseDto;
import org.springframework.stereotype.Service;

import kh.devspaceapi.model.dto.admin.stats.DailyViewCountResponseDto;
import kh.devspaceapi.repository.PostViewLogRepository;
import kh.devspaceapi.service.PostViewLogService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostViewLogServiceImpl implements PostViewLogService {

	// 일별 조회수 의존성주입
	private final PostViewLogRepository postViewLogRepository;

	// 일별 조회수를 List타입으로 return
	@Override
	public List<DailyViewCountResponseDto> getDailyViewCountBetween(LocalDate startDate, LocalDate endDate) {
		
		Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTsExclusive = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

		// postViewLogRepository의 getDailyViewCount()로 가져온 날짜와 조회수를 배열로 저장
//		return postViewLogRepository.getDailyViewCountBetween(startTs, endTsExclusive);
return null;
	}

	/*
	 * @List<Object[]> results는 날짜의 시작과 끝의 시간을 설정하고, 날짜별 연령대, 성별별 카운트값을 저장
	 * 
	 * @return map방식으로 배열된 dto를 반환해준다.
	 */
	@Override
	public List<AgeGenderDistributionResponseDto> getAgeGenderDistribution(LocalDate startDate, LocalDate endDate) {
//		List<Object[]> results = postViewLogRepository.getAgeGenderDistribution(startDate.atStartOfDay(),
//				endDate.atTime(23, 59, 59));
//		return results.stream().map(row -> new AgeGenderDistributionResponseDto((String) row[0], (String) row[1],
//				((Number) row[2]).longValue())).toList();
		return null;
	}

}
