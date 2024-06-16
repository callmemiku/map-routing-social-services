package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.dto.EventDTO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventRelationRepository {

    JdbcTemplate jdbcTemplate;

    public void saveEvents(
            List<EventDTO> events
    ) {
        jdbcTemplate.batchUpdate(
                """
                        insert into events(
                            id,
                            description,
                            system,
                            external_created,
                            completed,
                            region_name,
                            unom,
                            address,
                            external_completed
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var current = events.get(i);
                        ps.setObject(1, UUID.randomUUID()); // id
                        ps.setString(2, current.getName()); // description
                        ps.setString(3, current.getType().name()); // system
                        setStringNullSafe(ps, 4, current.getRegistrationDatetime()); // external_created
                        setStringNullSafe(ps, 5, current.getResolvedDatetime()); // completed
                        ps.setString(6, current.getRegion()); // region_name
                        setStringNullSafe(ps, 7, current.getUnom()); // unom
                        ps.setString(8, current.getAddress()); // address
                        setStringNullSafe(ps, 9, current.getEventEndedDatetime()); // external_completed
                    }

                    @Override
                    public int getBatchSize() {
                        return events.size();
                    }
                }
        );
    }

    private void setStringNullSafe(PreparedStatement ps, Integer number, Object any) throws SQLException {
        if (any == null) {
            ps.setNull(number, Types.VARCHAR);
        } else {
            ps.setString(number, any.toString());
        }
    }
}
