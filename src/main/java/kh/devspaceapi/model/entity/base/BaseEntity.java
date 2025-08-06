package kh.devspaceapi.model.entity.base;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BaseEntity {
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
