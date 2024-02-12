package org.dnd.timeet.common.utils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import org.dnd.timeet.common.exception.InternalServerError;

public class TimeUtils {

    public static Duration calculateTimeDiff(LocalTime totalEstimatedDuration, LocalTime totalActualDuration) {
        if (totalEstimatedDuration == null || totalActualDuration == null) {
            throw new InternalServerError(InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR,
                Collections.singletonMap("LocalTime", "LocalTime EstimatedDuration or ActualDuration is null"));
        }

        return Duration.between(totalEstimatedDuration, totalActualDuration);
    }

}
