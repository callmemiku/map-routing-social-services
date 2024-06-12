package ru.moscow.hackathon.coordinator.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class MultisheetXLSXDTO {

    @Getter
    List<SheetDTO> sheets;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SheetDTO {
        OperationType type;
        String sheetName;
    }
}
