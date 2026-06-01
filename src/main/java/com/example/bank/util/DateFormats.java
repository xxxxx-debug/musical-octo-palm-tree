package com.example.bank.util;

import java.time.format.DateTimeFormatter;

public final class DateFormats {

    public static final String PATTERN = "dd.MM.yyyy";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    private DateFormats() {
    }
}
