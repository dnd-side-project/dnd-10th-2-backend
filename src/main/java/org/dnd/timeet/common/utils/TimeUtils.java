package org.dnd.timeet.common.utils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import org.dnd.timeet.common.exception.InternalServerError;

public class TimeUtils {

    public static Duration calculateTimeDiff(LocalTime ActualDuration, LocalTime EstimatedDuration) {

        if (ActualDuration == null) {
            throw new InternalServerError(InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR,
                Collections.singletonMap("LocalTime", "ActualDuration is null"));
        }
        if (EstimatedDuration == null) {
            throw new InternalServerError(InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR,
                Collections.singletonMap("LocalTime", "EstimatedDuration is null"));
        }

        return Duration.between(EstimatedDuration, ActualDuration);
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
