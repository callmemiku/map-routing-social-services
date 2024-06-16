package ru.moscow.hackathon.coordinator.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ODSConnectionsDTO {
    String geoJSON;
    List<List<Double>> connected;
}
