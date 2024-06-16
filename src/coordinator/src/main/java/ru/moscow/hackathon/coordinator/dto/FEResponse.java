package ru.moscow.hackathon.coordinator.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Data
public class FEResponse {
    List<ODSConnectionsDTO> connections;
    Page<FENotificationDTO> page;
}
