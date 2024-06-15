package ru.moscow.hackathon.coordinator.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.entity.PowerEfficiencyEntity;

import java.util.UUID;

@Repository
public interface PowerEfficiencyJpaRepository extends JpaRepository<PowerEfficiencyEntity, UUID> {
    Page<PowerEfficiencyEntity> findAllBy(Pageable pageable);
}
