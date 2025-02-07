package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public enum Materials {

    A("Пенополистирол", -1, "-1", 40, 0.84),
    A1("Маты минераловатные прошивные", -1, "-1", 125, 1d),
    A2("Пенополистирол", -1, "-1", 100, 1.34),
    A3("Пенопласт ПХВ-1", -1, "-1", 125, 1.26),
    A4("Пенополистирол", 179625092, "из мелких бетонных блоков", 150, 1.34),
    A5("Газо- и пенобетон газо- и пено-силикат", 179625091, "из легкобетонных панелей", 300, 0.84),
    A6("Газо- и пенобетон газо- и пено-силикат", -1, "-1", 400, 0.84),
    A7("Плиты древесно-волокнистые и древесно-стружечные", 179625081, "гипсобетонные", 200, 2.3),
    A8("Газо- и пенобетон газо- и пено-силикат", -1, "-1", 600, 0.84),
    A9("Газо- и пенобетон газо- и пено-силикат", -1, "-1", 800, 0.84),
    A10("Газо- и пено- золобетон", 179625080, "бетонные", 800, 0.84),
    A11("Листы гипсовые обшивочные (сухая штукатурка)", 179625083, "гипсолитовые", 800, 0.84),
    A12("Газо- и пенобетон газо- и пено-силикат", 2048944, "каркас монолит-ж/б с заполнением пенобет.блоками с утеплител", 1000, 0.84),
    A13("Газо- и пенозолобетон", 2048942, "пеноблоки", 1000, 0.84),
    A14("Плиты древесно-волокнистые и древесно-стружечные", -1, "-1", 400, 2.3),
    A15("Газо- и пено- золобетон", 179625107, "легкобетонные блоки", 1200, 0.84),
    A16("Сосна и ель поперёк волокон", 179625085, "деревянные неоштукатуренные", 500, 2.3),
    A17("Сосна и ель вдоль волокон", 179625084, "деревянные брусовые", 500, 2.3),
    A18("Керамический пустотный", -1, "-1", 1400, 0.88),
    A19("Фанера клееная", -1, "-1", 600, 2.3),
    A20("Плиты древесно-волокнистые и древесно-стружечные", 179625088, "дощатые", 600, 2.3),
    A21("Кирпич керамический", 101812166, "Каменные, кирпичные", 1600, 0.88),
    A22("Бетон на доменных гранулированных шлаках", -1, "-1", 1800, 0.84),
    A23("Кирпичная кладка (кирпич глиняный)", 2048928, "кирпичный", 1800, 0.88),
    A24("Дуб поперек волокон", 179625113, "рубленые", 700, 2.3),
    A25("Дуб вдоль волокон", 2048931, "деревянные", 700, 2.3),
    A26("Плиты древесно-волокнистые и древесно-стружечные", 179625087, "деревянный каркас без обшивки", 800, 2.3),
    A27("Бетон на гравии или щебне из природного камня", 179625110, "монолитные (бетонные)", 2400, 0.84),
    A28("Железо-бетон", 179625111, "монолитные (ж-б)", 2500, 0.84),
    A29("Картон облицовочный", 179625100, "каркасно-обшивные", 1000, 2.3),
    A30("Плиты древесно-волокнистые и древесностружечные", 179625086, "деревянные оштукатуренные", 1000, 2.3);

    String name;
    Integer col769Reference;
    String btiData;
    Integer density;
    Double heatCapacity;

    public static Materials byColRef(String col769Reference) {

        if (col769Reference == null) {
            return null;
        }

        try {
            var num = Integer.parseInt(col769Reference);
            return EnumSet.allOf(Materials.class)
                    .stream()
                    .filter(it -> it.col769Reference.equals(num))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static Materials byMaterialBti(String material) {

        if (material == null) {
            return null;
        }

        try {
            return EnumSet.allOf(Materials.class)
                    .stream()
                    .filter(it -> it.btiData.contains(material) || material.contains(it.getBtiData()) || it.name.contains(material))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
