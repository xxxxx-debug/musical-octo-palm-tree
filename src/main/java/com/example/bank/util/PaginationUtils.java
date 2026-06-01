package com.example.bank.util;

import org.springframework.data.domain.PageRequest;

public final class PaginationUtils {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PaginationUtils() {
    }

    public static PageRequest toPageRequest(Integer page, Integer size) {
        int resolvedPage = page == null ? 1 : page;
        int resolvedSize = size == null ? DEFAULT_SIZE : size;

        if (resolvedPage < 1) {
            throw new IllegalArgumentException("page must be >= 1");
        }
        if (resolvedSize < 1 || resolvedSize > MAX_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_SIZE);
        }

        return PageRequest.of(resolvedPage - 1, resolvedSize);
    }

    public static int toApiPage(int zeroBasedPage) {
        return zeroBasedPage + 1;
    }
}
