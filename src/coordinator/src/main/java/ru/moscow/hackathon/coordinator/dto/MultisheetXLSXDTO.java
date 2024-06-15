package ru.moscow.hackathon.coordinator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty
    @Valid
    List<SheetDTO> sheets;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SheetDTO {
        @NotNull OperationType type;
        @NotBlank String sheetName;
        @NotNull Integer headerSize;
    }
}
