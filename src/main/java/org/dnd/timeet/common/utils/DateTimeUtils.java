package org.dnd.timeet.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * LocalDateTime 객체를 yyyy-MM-dd'T'HH:mm:ss 형식의 문자열로 포매팅한다.
     *
     * @param localDateTime LocalDateTime 객체
     * @return 포매팅된 문자열
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(FORMATTER);
    }
}

