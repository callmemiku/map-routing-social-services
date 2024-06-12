package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.entity.BuildingEntity;

import java.util.List;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FullBuildingInfoRepository {

    JdbcTemplate jdbcTemplate;
    /*info needed: {
        priority
        working_hours
        efficiency_class
        cooling speed?
    }
     */

    public List<BuildingEntity> info(
            List<String> unoms
    ) {
        var sql = String.format(
                """
                select
                ad.building_group as type,
                ad.ods_identification as ods,
                ped.energy_class as ec,
                ad.unom as unom,
                ard.geodata_center as geo
                from asupr_data ad
                left join power_efficiency_data ped on ad.address = ped.building
                left join address_registry_data ard on ad.unom = ard.unom
                where ad.unom in ('%s');
                """,
                String.join("','", unoms)
        );
        return jdbcTemplate.query(
                sql,
                (rs, rn) -> BuildingEntity.builder()
                        .centerCoordinates(rs.getString("geo"))
                        .odsIdentity(rs.getString("ods"))
                        .efficiency(rs.getString("ec"))
                        .type(rs.getString("type"))
                        .unom(rs.getString("unom"))
                        .build()
        );
    }
}
