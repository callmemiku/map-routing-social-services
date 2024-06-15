package ru.moscow.hackathon.coordinator.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.entity.WallsMaterialsEntity;

import java.util.UUID;

@Repository
public interface WallsMaterialsJpaRepository extends JpaRepository<WallsMaterialsEntity, UUID> {
    Page<WallsMaterialsEntity> findAllBy(Pageable pageable);
}
