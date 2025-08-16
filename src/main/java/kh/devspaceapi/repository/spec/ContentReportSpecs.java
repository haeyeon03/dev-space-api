package kh.devspaceapi.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import kh.devspaceapi.model.entity.ContentReport;
import kh.devspaceapi.model.entity.enums.ReportStatus;
import kh.devspaceapi.model.entity.enums.TargetType;

public class ContentReportSpecs {
	
	// 처리 상태별 검색
	public static Specification<ContentReport> statusIs(ReportStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

	// content type에 따른 검색 (news, board, comment)
    public static Specification<ContentReport> targetTypeIs(TargetType type) {
        return (root, query, cb) -> {
            if (type == null) return cb.conjunction();
            return cb.equal(root.get("targetType"), type);
        };
    }

    // 신고자 ID 부분검색 (대소문자 무시)
    public static Specification<ContentReport> reporterContains(String userIdLike) {
        return (root, query, cb) -> {
            if (userIdLike == null || userIdLike.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("reporter").get("userId")), "%" + userIdLike.toLowerCase() + "%");
        };
    }

}
