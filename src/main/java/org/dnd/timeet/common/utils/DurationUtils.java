package org.dnd.timeet.common.utils;

import java.time.Duration;
import java.time.LocalTime;

public class DurationUtils {

    /**
     * LocalTime 객체를 Duration으로 변환한다.
     * @param time LocalTime 객체
     * @return Duration 객체
     */
    public static Duration convertLocalTimeToDuration(LocalTime time) {
        int totalMinutes = time.getHour() * 60 + time.getMinute();
        return Duration.ofMinutes(totalMinutes);
    }

    /**
     * Duration 객체를 HH:mm 형식의 문자열로 포매팅한다.
     * @param duration Duration 객체
     * @return 포매팅된 시간 문자열 (HH:mm)
     */
    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public static void main(String[] args) {
        // 변환 테스트
        String timeString = "02:30";
        LocalTime time = LocalTime.parse(timeString);
        Duration duration = convertLocalTimeToDuration(time);
        System.out.println("Converted Duration: " + duration); // PT2H30M

        // 포매팅 테스트
        String formattedDuration = formatDuration(duration);
        System.out.println("Formatted Duration: " + formattedDuration); // 02:30
    }
}

