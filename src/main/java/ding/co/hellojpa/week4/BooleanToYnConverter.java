package ding.co.hellojpa.week4;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToYnConverter implements AttributeConverter<Boolean, String> {
    // 1. 자바(Boolean) -> DB(String) 저장 시
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) return "N"; // null 방어 로직
        return attribute ? "Y" : "N"; // true면 "Y", false면 "N"
    }

    // 2. DB(String) -> 자바 (Boolean)조회 시
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData == null) return false;
        return dbData.equalsIgnoreCase("Y"); // "Y"면 true, 아니면 false
    }
}
