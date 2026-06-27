package ding.co.hellojpa.week4;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    // 1. 엔티티 (Java) -> DB 컬럼으로 저장될 때 (암호화)
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return "ENCRYPTED_" + attribute; // 실제 실무에서는 AES-256 등의 암호화 로직 수행
    }

    // 2. DB 컬럼 -> 엔티티(JAva)로 읽어올 때 (복호화)
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return dbData.replace("ENCRYPTER_", ""); // 실제 실무에서는 복호화 로직 수행
    }
}
