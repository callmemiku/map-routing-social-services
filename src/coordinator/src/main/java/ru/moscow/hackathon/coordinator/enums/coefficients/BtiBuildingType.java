package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum BtiBuildingType {

    A(List.of("коттедж", "жилое строениe").stream().map(String::toUpperCase).toList(), 18),
    A1(List.of("гостиница").stream().map(String::toUpperCase).toList(), 20),
    A2(List.of("ясли детсад-яслидетское дошкольное учреждение").stream().map(String::toUpperCase).toList(), 23),
    A3(List.of("школа школа-интернат интернат спецшкола").stream().map(String::toUpperCase).toList(), 16),
    A4(List.of("профтехучилище", "техникумшкола", "искусств").stream().map(String::toUpperCase).toList(), 16),
    A5(List.of("университет", "институт", "административно-учебное", "учебно-методический центр").stream().map(String::toUpperCase).toList(), 18),
    A6(List.of("приемное отделение ", "лечебный корпус", "лабораторно-клинический корпус", "хирургический корпус", "терапевтический корпус", "лечебное", "санитарное", "медвытрезвитель", "лазарет").stream().map(String::toUpperCase).toList(), 25),
    A8(List.of("детский санаторий", "санаторий").stream().map(String::toUpperCase).toList(), 18),
    A9(List.of("спортивное", "спортивная школа", "школа спортивного мастерства", "культурно-спортивный корпус").stream().map(String::toUpperCase).toList(), 15),
    A10(List.of("баня-прачечная").stream().map(String::toUpperCase).toList(), 25),
    A11(List.of("фабрика-прачечная", "сушилка").stream().map(String::toUpperCase).toList(), 15),
    A12(List.of("универсам", "универмаг", "кафе", "магазин", "автомагазин").stream().map(String::toUpperCase).toList(), 12),
    A13(List.of("кинотеатр").stream().map(String::toUpperCase).toList(), 14),
    A14(List.of("театр").stream().map(String::toUpperCase).toList(), 16),
    A15(List.of("клуб столовая").stream().map(String::toUpperCase).toList(), 16),
    A16(List.of("комбинат общественного питания", "столовая и спортзал", "столовая и казарма", "кафе-столовая", "спортзал, столовая", "столовая", "учреждение и столовая", "столовая и учpеждение", "кафе-пельменная", "кафе-мороженое", "кафе", "кафетерий", "буфет", "ресторан", "чебуречная", "общественное питание", "молочно-раздаточный пункт").stream().map(String::toUpperCase).toList(), 16),
    A17(List.of("коммунально-бытовое", "мастерские", "ремонтные", "салон-парикмахерская").stream().map(String::toUpperCase).toList(), 18),
    A18(List.of("стоянка машин, гараж", "автозаправочная станция", "мойка автомашин", "автостоянка открытого типа", "автостоянка", "автостоянка подземная", "мойка-гараж", "профилакторий для машин").stream().map(String::toUpperCase).toList(), 5),
    A19(List.of("автосервис", "ремонтная автомастерская", "автомастерская мастерская по ремонту автотранспорта").stream().map(String::toUpperCase).toList(), 16),
    A20(List.of("бюро пропусков", "склад и мастерская", "цех по ремонту").stream().map(String::toUpperCase).toList(), 16),
    ;

    List<String> types;
    Integer temperature;

    public static Integer byType(String type) {
        if (type == null || type.isEmpty() || type.isBlank()) {
            return 18;
        } else {
            return EnumSet.allOf(BtiBuildingType.class)
                    .stream()
                    .filter(it -> it.types.contains(type.toUpperCase()))
                    .findFirst()
                    .orElse(A)
                    .temperature;
        }
    }
}
