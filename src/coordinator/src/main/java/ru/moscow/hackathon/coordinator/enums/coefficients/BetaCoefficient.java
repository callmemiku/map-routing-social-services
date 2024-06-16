package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum BetaCoefficient {
    K_46_MKD(
            3,
            List.of(
                    "сегм"
            ),
            46
    ),
    K_40_MKD(
            3,
            List.of(
                    "железобет"
            ),
            40
    ),
    K_40_SOC(
            1,
            List.of(
                    "желез",
                    "карк",
                    "бето"
            ),
            40
    ),
    K_40_IND(
            2,
            List.of(
                    "желез",
                    "карк",
                    "бето"
            ),
            40
    ),
    K_65_MKD(
            3,
            List.of(
                    "кирп"
            ),
            65
    ),
    K_25_IND(
            2,
            List.of(
                    "кирп"
            ),
            25
    ),
    K_60_MKD(
            3,
            List.of(
                    "кирп",
                    "обл"
            ),
            60
    ),
    K_20_SOC(
            1,
            List.of(
                    "дерев",
                    "брус",
                    "рубл"
            ),
            20
    ),
    K_20_MKD(
            3,
            List.of(
                    "дерев",
                    "брус",
                    "рубл"
            ),
            20
    ),
    K_55_MKD(
            3,
            List.of(
                    "пане"
            ),
            55
    ),
    K_50_SOC(
            1,
            List.of(
                    "пане"
            ),
            50
    ),
    K_45_IND(
            2,
            List.of(
                    "пане"
            ),
            45
    ),
    ;


    Integer coefficientByBuildingType;
    List<String> materialReference;
    Integer coefficient;

    public static Integer priority(
            Integer byGroup,
            String material
    ) {

        if (material == null) {
            return switch (byGroup) {
                case 1 -> 40;
                case 2 -> 20;
                case 3 -> 60;
                default -> throw new IllegalArgumentException("???");
            };
        }

        var result = EnumSet.allOf(BetaCoefficient.class)
                .stream()
                .filter(it -> it.coefficientByBuildingType.equals(byGroup))
                .filter(it -> it.materialReference.stream().anyMatch(material::contains))
                .findFirst();
        if (result.isPresent()) {
            return result.get().coefficient;
        } else {
            return switch (byGroup) {
                case 1 -> 40;
                case 2 -> 20;
                case 3 -> 60;
                default -> throw new IllegalArgumentException("???");
            };
        }
    }
}
