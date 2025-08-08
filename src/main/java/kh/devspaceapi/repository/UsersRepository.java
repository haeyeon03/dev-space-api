package kh.devspaceapi.repository;


import kh.devspaceapi.model.entity.Users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsersRepository  extends JpaRepository<Users, String> {
	
	@Query("SELECT u.gender, COUNT(u) " +
		       "FROM Users u " +
		       "GROUP BY u.gender")
		List<Object[]> countUserByGender();
}
