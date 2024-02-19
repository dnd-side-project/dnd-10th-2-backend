package org.dnd.timeet.meeting.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class MeetingScheduler {

    private final MeetingAsyncService meetingAsyncService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final MeetingRepository meetingRepository;

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

    @Scheduled(fixedRate = 60000) // 60000ms = 1분
    public void scheduleMeetingEnd() {
        List<Meeting> meetingsByStatusInProgress = meetingRepository.findMeetingsByStatusInProgress();

        LocalDateTime now = LocalDateTime.now();

        meetingsByStatusInProgress.forEach(meeting -> {
            // 남은 시간이 0이거나 음수인 경우 회의를 종료
            Duration remainingTime = meeting.calculateRemainingTime();
            if (remainingTime.isZero() || remainingTime.isNegative()) {
                meetingAsyncService.endScheduledMeeting(meeting);
            }
        });
    }

}
