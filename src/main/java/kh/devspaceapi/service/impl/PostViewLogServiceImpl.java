package kh.devspaceapi.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
	public List<DailyViewCountResponseDto> getDailyViewCountBetween(Timestamp startDate, Timestamp endDate) {
		
		//results는 postViewLogRepository의 getDailyViewCount()로 가져온 날짜와 조회수를 배열로 저장
		List<Object[]> results = postViewLogRepository.getDailyViewCountBetween(startDate, endDate);
		/*
		 * @return dtoList는 results에 저장된 데이터를 date와 count를 DailyViewCountResponseDto를
		 * 이용해 반복문을 사용하여 날짜별 조회수를 반환해준다.
		 */
		List<DailyViewCountResponseDto> dtoList = new ArrayList<>();
		for (Object[] row : results) {
			String date = (String) row[0];
			Long count = ((Number) row[1]).longValue();
			dtoList.add(new DailyViewCountResponseDto(date, count));
		}
		return dtoList;
	}

}
