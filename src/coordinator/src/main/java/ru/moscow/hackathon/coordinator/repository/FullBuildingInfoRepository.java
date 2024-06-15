package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.entity.BuildingEntity;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FullBuildingInfoRepository {

    JdbcTemplate jdbcTemplate;

    public BuildingEntity info(
            String unom
    ) {
        try {
            return jdbcTemplate.queryForObject(
                    """
                select
                ad.building_group as type,
                ad.ods_identification as ods,
                ped.energy_class as ec,
                ad.unom as unom,
                ard.geodata_center as geo,
                ad.address as address,
                ad.ods_address as ods_adr,
                ad.consumer as consumer,
                ad.address_full as adr_full,
                ad.warm_point_id as wp_id,
                mcd.address as tp_addr,
                mcd.heat_source as tp_hs,
                mcd.heating_station_type as tp_st  
                from asupr_data ad
                left join power_efficiency_data ped on ad.address = ped.building
                left join address_registry_data ard on ad.unom = ard.unom
                left join moek_connection_data mcd on ad.warm_point_id = mcd.heating_station_number
                where ad.unom = ? limit 1;
                """,
                (rs, rn) -> BuildingEntity.builder()
                        .centerCoordinates(rs.getString("geo"))
                        .odsIdentity(rs.getString("ods"))
                        .efficiency(rs.getString("ec"))
                        .type(rs.getString("type"))
                        .unom(rs.getString("unom"))
                        .address(rs.getString("address"))
                        .odsAddress(rs.getString("ods_adr"))
                        .warmPointId(rs.getString("wp_id"))
                        .addressFull(rs.getString("adr_full"))
                        .consumer(rs.getString("consumer"))
                        .tpAddress(rs.getString("tp_addr"))
                        .tpHeatSource(rs.getString("tp_hs"))
                        .tpType(rs.getString("tp_st"))
                        .build(),
                unom
        );
        } catch (Exception e) {
            log.error("Не удалось найти информацию о здании: {}", e.getMessage());
            return null;
        }
    }
}
