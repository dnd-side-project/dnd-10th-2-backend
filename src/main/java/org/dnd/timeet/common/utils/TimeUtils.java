package org.dnd.timeet.common.utils;

import java.time.Duration;
import java.util.Collections;
import org.dnd.timeet.common.exception.InternalServerError;

public class TimeUtils {

    public static Duration calculateTimeDiff(Duration actualDuration, Duration estimatedDuration) {

        if (actualDuration == null) {
            throw new InternalServerError(InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR,
                Collections.singletonMap("LocalTime", "ActualDuration is null"));
        }
        if (estimatedDuration == null) {
            throw new InternalServerError(InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR,
                Collections.singletonMap("LocalTime", "estimatedDuration is null"));
        }
        // actualDuration - estimatedDuration
        return actualDuration.minus(estimatedDuration);
    }

    public static String formatDuration(Duration duration) {
        long hours = duration.abs().toHours();
        long minutes = duration.minusHours(duration.toHours()).abs().toMinutes();
        String prefix = "+";
        if (duration.isNegative()) {
            prefix = "-";
        }
        return prefix + String.format("%02d:%02d", hours, minutes);
    }

}
