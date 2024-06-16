package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.moscow.hackathon.coordinator.entity.BuildingEntity;
import ru.moscow.hackathon.coordinator.entity.PEInfo;

import java.util.Locale;

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
            var building = jdbcTemplate.queryForObject(
                    """
                            select
                            ad.building_group as type,
                            ad.ods_identification as ods,
                            ad.unom as unom,
                            ard.geodata_center as geo,
                            ad.address as address,
                            ad.ods_address as ods_adr,
                            ad.consumer as consumer,
                            ad.address_full as adr_full,
                            ad.warm_point_id as wp_id,
                            mcd.address as tp_addr,
                            mcd.heat_source as tp_hs,
                            mcd.heating_station_type as tp_st,
                            pcd.exterior_wall_material as ewm,
                            bd.exterior_wall_material as ewm_bti,
                            ard.simple_address as sad
                            from asupr_data ad
                            left join address_registry_data ard on ad.unom = ard.unom
                            left join moek_connection_data mcd on ad.warm_point_id = mcd.heating_station_number
                            left join  performance_characteristics_data pcd on ad.unom = pcd.unom
                            left join bti_data bd on ad.unom = bd.unom
                            where ad.unom = ? limit 1;
                            """,
                    (rs, rn) -> BuildingEntity.builder()
                            .centerCoordinates(rs.getString("geo"))
                            .odsIdentity(rs.getString("ods"))
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
                            .material(rs.getString("ewm"))
                            .materialBTI(rs.getString("ewm_bti"))
                            .simpleAddress(rs.getString("sad"))
                            .build(),
                    unom
            );

            var template = """
                            SELECT 
                            full_square_heated as fsh, 
                            employees_number as en, 
                            energy_class as ec,
                            floors_number as fn, 
                            plainto_tsquery('%s')
                            FROM power_efficiency_data ped
                            LEFT JOIN power_efficiency_data_vector pedv ON ped.id = pedv.id
                            WHERE "building_vector" @@ plainto_tsquery('%s')
                            ORDER BY ts_rank("building_vector", plainto_tsquery('%s')) DESC;
                    """;

            PEInfo peInfo;
            try {
                var sad = prepare(building.getSimpleAddress());
                peInfo = jdbcTemplate.queryForObject(
                        String.format(
                                template,
                                sad,
                                sad,
                                sad
                        ),
                        (rs, rn) -> PEInfo.builder()
                                .heated(rs.getDouble("fsh"))
                                .employees(rs.getInt("en"))
                                .floors(rs.getInt("fn"))
                                .efficiency(rs.getString("ec"))
                        .build()

                );

                if (peInfo == null) {
                    throw new RuntimeException();
                }

                building.setFloors(peInfo.getFloors());
                building.setFullHeatedSquare(peInfo.getHeated());
                building.setEfficiency(peInfo.getEfficiency());
                building.setEmployeeCount(peInfo.getEmployees());
                return building;
            } catch (Exception e) {
                log.error("По адресу из регистра не найдено здание.");
            }

            try {
                var address = prepare(building.getAddress());
                peInfo = jdbcTemplate.queryForObject(
                        String.format(
                                template,
                                address,
                                address,
                                address
                        ),
                        (rs, rn) -> PEInfo.builder()
                                .heated(rs.getDouble("fsh"))
                                .employees(rs.getInt("en"))
                                .floors(rs.getInt("fn"))
                                .efficiency(rs.getString("ec"))
                                .build()

                );

                if (peInfo == null) {
                    throw new RuntimeException();
                }

                building.setFloors(peInfo.getFloors());
                building.setFullHeatedSquare(peInfo.getHeated());
                building.setEfficiency(peInfo.getEfficiency());
                building.setEmployeeCount(peInfo.getEmployees());
                return building;
            } catch (Exception e) {
                log.error("По адресу из АСУПР не найдено здание.");
            }

            return building;
        } catch (Exception e) {
            return null;
        }
    }

    private String prepare(String source) {
        return source.toUpperCase(Locale.ROOT)
                .replaceAll("ШОССЕ", "Ш.")
                .replaceAll("УЛИЦА", "УЛ.")
                .replaceAll("ДОМ", "Д.")
                .replaceAll("ПЕРЕУЛОК", "ПЕР.")
                .replaceAll("НАБЕРЕЖНАЯ", "НАБ.")
                .replaceAll("СТРОЕНИЕ", "СТР.")
                .replaceAll("БУЛЬВАР", "Б-Р")
                .replaceAll("КОРПУС", "КОРП.")
                .replaceAll("МИКРОРАЙОН", "МКР")
                .replaceAll("ПЛОЩАДЬ", "ПЛ.")
                .replaceAll("ПРОСПЕКТ", "ПР-КТ")
                .replaceAll("КВАРТАЛ", "КВ.");
    }
}
