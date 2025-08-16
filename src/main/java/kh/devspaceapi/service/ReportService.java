package kh.devspaceapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kh.devspaceapi.model.dto.admin.stats.ReportDetailDto;
import kh.devspaceapi.model.dto.admin.stats.ReportListDto;
import kh.devspaceapi.model.dto.admin.stats.UpdateReportStatusRequestDto;
import kh.devspaceapi.model.entity.enums.ReportStatus;
import kh.devspaceapi.model.entity.enums.TargetType;

public interface ReportService {

	// 신고된 content 페이징 기법 출력 service
	Page<ReportListDto> getReports(ReportStatus status, TargetType type, String reporterLike, Pageable pageable);
	
	// 신고된 content 상세확인 service
	ReportDetailDto getReportDetail(Long reportId);

	// 신고된 content 상태 update service
	ReportDetailDto updateReportStatus(Long reportId, String handlerUserId, UpdateReportStatusRequestDto req);

}
