package org.dnd.timeet.meeting.application;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.exception.BadRequestError;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class MeetingScheduler {

    private final MeetingAsyncService meetingAsyncService;
    private final ScheduledExecutorService scheduledExecutorService;

    public void scheduleMeetingStart(Long meetingId, LocalDateTime startTime) {
        long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), startTime);
        if (delay < 0) {
            throw new BadRequestError(BadRequestError.ErrorCode.VALIDATION_FAILED,
                Collections.singletonMap("startTime", "startTime is past"));
        }

        // 스케줄러 생성
        scheduledExecutorService.schedule(() ->
            meetingAsyncService.startScheduledMeeting(meetingId), delay, TimeUnit.MILLISECONDS);
    }
}
