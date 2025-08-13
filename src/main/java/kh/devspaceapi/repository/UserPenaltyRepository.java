package kh.devspaceapi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.entity.UserPenalty;

public interface UserPenaltyRepository extends JpaRepository<UserPenalty, Integer>{


}
