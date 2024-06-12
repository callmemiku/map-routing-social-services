package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.dto.ConfirmedEventDTO;
import ru.moscow.hackathon.coordinator.enums.SourceType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventRepository {

    JdbcTemplate template;

    public List<ConfirmedEventDTO> findEvents() {
        return template.query(
                """
                    select 
                    id,
                    description as name,
                    system as type,
                    external_created as registrationDatetime,
                    completed as resolvedDatetime,
                    region_name as region,
                    unom,
                    address,
                    external_completed as eventEndedDatetime
                    from events
                """,
                (rs, rowNum) -> {
                    var result = new ConfirmedEventDTO();
                    result.setId(
                            rs.getObject("id", UUID.class)
                    );
                    result.setName(
                            rs.getString("name")
                    );
                    result.setType(
                            SourceType.valueOf(
                                    rs.getString("type").toUpperCase()
                            )
                    );
                    result.setRegion(
                            rs.getString("region")
                    );
                    result.setUnom(
                            rs.getString(
                                    "unom"
                            )
                    );
                    result.setAddress(
                            rs.getString("address")
                    );
                    return result;
                }
        );
    }
}
