package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.dto.FENotificationDTO;
import ru.moscow.hackathon.coordinator.dto.ODSConnectionsDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OdsConnectionRepository {

    JdbcTemplate jdbcTemplate;

    public List<ODSConnectionsDTO> connectionsDTO(
            Page<FENotificationDTO> dtos
    ) {

        Map<String, List<FENotificationDTO>> grouped = dtos.stream()
                .filter(it -> Objects.nonNull(it.getBuilding()))
                .collect(
                        Collectors.groupingBy(
                                it -> it.getBuilding().getOdsAddress()
                        )
                );

        Map<String, String> geoJSONs = new HashMap<>();

        var template = """
                        SELECT 
                        ped.geodata_center,
                        plainto_tsquery('%s')
                        FROM address_registry_data ped
                        LEFT JOIN address_registry_data_vector pedv ON ped.id = pedv.id
                        WHERE "full_address_vector" @@ plainto_tsquery('%s')
                        ORDER BY ts_rank("full_address_vector", plainto_tsquery('%s')) DESC limit 1;
                """;

        grouped.keySet()
                .forEach(it -> {
                            try {
                                var geoJSON = jdbcTemplate.queryForObject(
                                        String.format(
                                                template,
                                                it,
                                                it,
                                                it
                                        ),
                                        (rs, rn) -> rs.getString(1)
                                );
                                geoJSONs.put(it, geoJSON);
                            } catch (Exception e) {
                                log.error("[ODS] {}", e.getMessage());
                            }
                        }
                );
        return grouped.entrySet()
                .stream()
                .map(
                        e -> {
                            if (geoJSONs.containsKey(e.getKey())) {
                                var coords = e.getValue()
                                        .stream()
                                        .map(it -> {
                                            var geo = it.getBuilding().getCenterCoordinates();
                                            Pattern pattern = Pattern.compile("\\[(.*?), (.*?)\\]");
                                            Matcher matcher = pattern.matcher(geo);

                                            if (matcher.find()) {
                                                String longitude = matcher.group(1); // 37.828189394
                                                String latitude = matcher.group(2); // 55.717482785
                                                return List.of(
                                                    Double.parseDouble(longitude),
                                                    Double.parseDouble(latitude)
                                                );
                                            } else return null;
                                        }).filter(Objects::nonNull)
                                        .toList();
                                return ODSConnectionsDTO.builder()
                                        .geoJSON(geoJSONs.get(e.getKey()))
                                        .connected(coords)
                                        .build();
                            } else {
                                return null;
                            }
                        }
                ).filter(Objects::nonNull)
                .toList();
    }
}
