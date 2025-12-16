package com.example.demo.tracing;

import brave.baggage.BaggageField;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@UtilityClass
@Slf4j
public class BaggageUtils {
    public static String get(String name) {
        var field = BaggageField.getByName(validateFieldName(name));
        if (field == null) {
            log.warn("get: field '{}' is null", name);
            return null;
        }
        return field.getValue();
    }

    public static void set(String name, String value) {
        var field = BaggageField.getByName(validateFieldName(name));
        if (field == null) {
            log.warn("set: field '{}' is null", name);
            return;
        }
        field.updateValue(value);
    }

    private static String validateFieldName(String fieldName) {
        if (fieldName == null) throw new NullPointerException("fieldName == null");
        fieldName = fieldName.toLowerCase(Locale.ROOT).trim();
        if (fieldName.isEmpty()) throw new IllegalArgumentException("fieldName is empty");
        return fieldName;
    }
}
