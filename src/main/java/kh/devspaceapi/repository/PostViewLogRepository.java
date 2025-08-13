package kh.devspaceapi.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.dto.admin.stats.DailyViewCountResponseDto;
import kh.devspaceapi.model.entity.PostViewLog;

public interface PostViewLogRepository extends JpaRepository<PostViewLog, Long> {

}
