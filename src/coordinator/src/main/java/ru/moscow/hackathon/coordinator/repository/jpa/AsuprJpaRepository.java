package ru.moscow.hackathon.coordinator.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.entity.AsuprEntity;

import java.util.UUID;

@Repository
public interface AsuprJpaRepository extends JpaRepository<AsuprEntity, UUID> {
    Page<AsuprEntity> findAllBy(Pageable pageable);
}
