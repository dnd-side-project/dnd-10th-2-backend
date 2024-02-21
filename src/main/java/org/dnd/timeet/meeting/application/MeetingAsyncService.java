package org.dnd.timeet.meeting.application;

import lombok.RequiredArgsConstructor;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingAsyncService {

    private final MeetingRepository meetingRepository;
    private final Logger logger = LoggerFactory.getLogger(MeetingAsyncService.class);

//    @Transactional
//    @Async // 비동기 작업 실행시 발생하는 에러 처리
//    public void startScheduledMeeting(Long meetingId) {
//        try {
//            Meeting meeting = meetingRepository.findById(meetingId)
//                .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
//                    Collections.singletonMap("MeetingId", "Meeting not found")));
//
//            meeting.startMeeting();
//            meetingRepository.save(meeting);
//        } catch (Exception e) {
//            logger.error("Error starting scheduled meeting", e);
//        }
//    }
}