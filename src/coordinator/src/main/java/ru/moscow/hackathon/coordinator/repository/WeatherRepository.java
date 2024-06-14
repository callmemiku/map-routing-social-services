package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.dto.weather.WeatherToDateDTO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WeatherRepository {

    JdbcTemplate jdbcTemplate;

    public void saveWeathers(
            List<WeatherToDateDTO> weathers
    ) {
        jdbcTemplate.batchUpdate(
                "insert into weathers(id, date_yyyy, date_dd, temperature) values (?,?,?,?) on conflict (date_yyyy) do nothing",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var curr = weathers.get(i);
                        ps.setObject(1, UUID.randomUUID());
                        ps.setString(2, curr.getDateYYYY());
                        ps.setString(3, curr.getDateDD());
                        ps.setDouble(4, curr.getTemperature());
                    }

                    @Override
                    public int getBatchSize() {
                        return weathers.size();
                    }
                }
        );
    }

    public Map<String, Double> weathers() {
        var result = new HashMap<String, Double>();
        jdbcTemplate.query(
                "select date_yyyy, temperature from weathers",
                (rs, rowNum) -> result.put(
                        rs.getString(1), rs.getDouble(2)
                )
        );
        return result;
    }
}
