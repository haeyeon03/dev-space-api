package kh.devspaceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.model.entity.Users;

public interface UserRepository extends JpaRepository<Users, String>{
	
	//users 남여 성별 count 메서드 "M"or"F"
	long countByGender(String gender);

}
