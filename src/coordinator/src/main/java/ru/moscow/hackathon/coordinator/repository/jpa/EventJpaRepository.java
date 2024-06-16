package ru.moscow.hackathon.coordinator.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.dto.EventDTO;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventJpaRepository extends JpaRepository<EventDTO, UUID> {
    Page<EventDTO> findAllBy(Pageable pageable);
    Page<EventDTO> findAllByNameInAndEventEndedDatetimeIsNull(List<String> in, Pageable pageable);
}
