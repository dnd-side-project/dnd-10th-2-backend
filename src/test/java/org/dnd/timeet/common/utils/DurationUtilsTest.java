package org.dnd.timeet.common.utils;

import java.time.Duration;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

public class DurationUtilsTest {

    @Test
    public void test() {
        // 변환 테스트
        String timeString = "02:30";
        LocalTime time = LocalTime.parse(timeString);
        Duration duration = DurationUtils.convertLocalTimeToDuration(time);
        System.out.println("Converted Duration: " + duration); // PT2H30M

        // 포매팅 테스트
        String formattedDuration = DurationUtils.formatDuration(duration);
        System.out.println("Formatted Duration: " + formattedDuration); // 02:30
    }
}
