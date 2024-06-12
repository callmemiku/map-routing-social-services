package ru.moscow.hackathon.coordinator.dto;

import org.springframework.http.HttpStatus;

public record StatusDTO
        (
                HttpStatus status,
                String message,
                String sheetName
         )
{
}
