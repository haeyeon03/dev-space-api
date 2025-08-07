package kh.devspaceapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kh.devspaceapi.model.entity.base.BaseEntity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Users extends BaseEntity {
    @Id
    @Column(name = "USER_ID")
    private String userId;
    private String password;
    private String nickname;
    private String gender;
    private String provider;
    private String email;
    private String role;
    private LocalDate birthdate;
}
