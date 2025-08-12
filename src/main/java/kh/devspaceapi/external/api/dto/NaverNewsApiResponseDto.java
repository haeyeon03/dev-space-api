package kh.devspaceapi.external.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverNewsApiResponseDto {
    private String lastBuildDate; // API 호출 시간
    private int total; // API 호출 결과 건수
    private int start;
    private int display;
    private List<NaverNewsApiItem> items; // 실데이터
}
