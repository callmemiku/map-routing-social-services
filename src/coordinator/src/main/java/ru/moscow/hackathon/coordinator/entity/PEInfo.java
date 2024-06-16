package ru.moscow.hackathon.coordinator.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PEInfo {
    Integer employees, floors;
    String efficiency;
    Double heated;
}
