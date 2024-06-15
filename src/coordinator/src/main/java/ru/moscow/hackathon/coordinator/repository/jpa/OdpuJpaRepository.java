package ru.moscow.hackathon.coordinator.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.entity.MoekConnectionEntity;
import ru.moscow.hackathon.coordinator.entity.OdpuEntity;

import java.util.UUID;

@Repository
public interface OdpuJpaRepository extends JpaRepository<OdpuEntity, UUID> {
    Page<OdpuEntity> findAllBy(Pageable pageable);
}
