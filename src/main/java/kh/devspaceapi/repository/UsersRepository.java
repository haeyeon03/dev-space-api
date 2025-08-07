package kh.devspaceapi.repository;


import kh.devspaceapi.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository  extends JpaRepository<Users, String> {
}
