package kh.devspaceapi.model.dto.admin.stats;

import kh.devspaceapi.model.entity.enums.ReportStatus;
import lombok.Data;

@Data
public class UpdateReportStatusRequestDto {
	
	// PROCESSING or COMPLETED
	private ReportStatus status;
	
	// 선택 입력
    private String handlerNote;

}
