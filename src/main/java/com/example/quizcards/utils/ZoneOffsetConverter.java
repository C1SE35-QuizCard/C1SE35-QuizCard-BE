package com.example.quizcards.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.ZoneId;
import java.time.ZoneOffset;

@Converter
public class ZoneOffsetConverter implements AttributeConverter<ZoneOffset, String> {
    @Override
    public String convertToDatabaseColumn(ZoneOffset zoneOffset) {
        if (zoneOffset == null) {
            return null;
        }

        return zoneOffset.getId();
    }

    @Override
    public ZoneOffset convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        try {
            return ZoneOffset.of(s);
        } catch (Exception e) {
            // Handle the case where the string is not a valid ZoneId
            throw new IllegalArgumentException("Invalid ZoneId string: " + s, e);
        }
    }
}
