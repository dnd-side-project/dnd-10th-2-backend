package org.dnd.timeet.common.utils;

import java.time.Duration;
import java.time.LocalTime;

public class DurationUtils {

    /**
     * LocalTime 객체를 Duration으로 변환한다.
     *
     * @param time LocalTime 객체
     * @return Duration 객체
     */
    public static Duration convertLocalTimeToDuration(LocalTime time) {
        int totalSeconds = time.toSecondOfDay(); // 시, 분, 초를 모두 초 단위로 변환
        return Duration.ofSeconds(totalSeconds); // 변환된 초를 바탕으로 Duration 생성
    }

    /**
     * Duration 객체를 HH:mm:ss 형식의 문자열로 포매팅한다.
     *
     * @param duration Duration 객체
     * @return 포매팅된 시간 문자열 (HH:mm:ss)
     */
    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

