package com.example.bank.util;

import com.example.bank.api.error.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class DateFormatUtils {

    private DateFormatUtils() {
    }

    public static String format(LocalDate date) {
        return date.format(DateFormats.FORMATTER);
    }

    public static LocalDate parse(String value) {
        try {
            return LocalDate.parse(value, DateFormats.FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Invalid date format, expected " + DateFormats.PATTERN);
        }
    }
}
