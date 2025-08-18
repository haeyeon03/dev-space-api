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
	public List<DailyViewCountResponseDto> getDailyViewCountBetween(Timestamp startDate, Timestamp endDate) {
		
		List<Object[]> results = postViewLogRepository.getDailyViewCountBetween(startDate, endDate);
        List<DailyViewCountResponseDto> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            String date = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            dtoList.add(new DailyViewCountResponseDto(date, count));
        }

        return dtoList;
	}

	/*
	 * @List<Object[]> results는 날짜의 시작과 끝의 시간을 설정하고, 날짜별 연령대, 성별별 카운트값을 저장
	 * 
	 * @return map방식으로 배열된 dto를 반환해준다.
	 */
	@Override
	public List<AgeGenderDistributionResponseDto> getAgeGenderStats(LocalDate startDate, LocalDate endDate) {
		List<Object[]> results = postViewLogRepository.getAgeGenderStats(startDate.atStartOfDay(),
				endDate.atTime(23, 59, 59));
		return results.stream().map(row -> new AgeGenderDistributionResponseDto((String) row[0], (String) row[1],
				((Number) row[2]).longValue())).toList();
		
	}

}
