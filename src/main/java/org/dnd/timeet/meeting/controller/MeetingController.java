package org.dnd.timeet.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.common.security.annotation.ReqUser;
import org.dnd.timeet.common.utils.ApiUtils;
import org.dnd.timeet.common.utils.ApiUtils.ApiResult;
import org.dnd.timeet.meeting.application.MeetingService;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.dnd.timeet.meeting.dto.MeetingCreateResponse;
import org.dnd.timeet.meeting.dto.MeetingCurrentDurationResponse;
import org.dnd.timeet.meeting.dto.MeetingInfoResponse;
import org.dnd.timeet.meeting.dto.MeetingMemberInfoResponse;
import org.dnd.timeet.meeting.dto.MeetingReportInfoResponse;
import org.dnd.timeet.meeting.dto.MeetingReportResponse;
import org.dnd.timeet.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회의 컨트롤러", description = "Meeting API입니다.")
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    @Operation(summary = "회의 생성", description = "회의를 생성한다.")
    public ResponseEntity<ApiResult<MeetingCreateResponse>> createMeeting(
        @RequestBody @Valid MeetingCreateRequest meetingCreateRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Meeting savedMeeting = meetingService.createMeeting(meetingCreateRequest, userDetails.getMember());
        MeetingCreateResponse meetingCreateResponse = MeetingCreateResponse.from(savedMeeting);

        return ResponseEntity.ok(ApiUtils.success(meetingCreateResponse));
    }

    @PostMapping("/{meeting-id}/attend")
    @Operation(summary = "회의 참가", description = "회의에 참가한다.")
    public ResponseEntity<ApiResult<MeetingCreateResponse>> attendMeeting(
        @PathVariable("meeting-id") Long meetingId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Meeting savedMeeting = meetingService.addParticipantToMeeting(meetingId, userDetails.getMember());
        MeetingCreateResponse meetingCreateResponse = MeetingCreateResponse.from(savedMeeting);

        return ResponseEntity.ok(ApiUtils.success(meetingCreateResponse));
    }

    @PatchMapping("/{meeting-id}/end")
    @Operation(summary = "회의 종료", description = "회의를 종료한다.")
    public ResponseEntity<ApiResult<MeetingReportResponse>> closeMeeting(
        @PathVariable("meeting-id") Long meetingId,
        @ReqUser Member member) {
        meetingService.endMeeting(meetingId, member.getId());

        MeetingReportInfoResponse meetingReportInfoResponse = meetingService.createReport(meetingId);
        MeetingReportResponse meetingReportResponse = new MeetingReportResponse(meetingReportInfoResponse);

        return ResponseEntity.ok(ApiUtils.success(meetingReportResponse));
    }

    @GetMapping("/{meeting-id}")
    @Operation(summary = "단일 회의 조회", description = "지정된 id에 해당하는 회의를 조회한다.")
    public ResponseEntity<ApiResult<MeetingInfoResponse>> getTimerById(@PathVariable("meeting-id") Long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        MeetingInfoResponse meetingInfoResponse = MeetingInfoResponse.from(meeting);

        return ResponseEntity.ok(ApiUtils.success(meetingInfoResponse));
    }

    @Operation(summary = "소요 시간 조회", description = "웹소켓을 통해 회의의 소요 시간을 조회한다.")
    @MessageMapping("/meeting/{meeting-id}/current-duration")
    @SendTo("/topic/meeting/{meeting-id}/current-duration")
    public ResponseEntity<ApiResult<MeetingCurrentDurationResponse>> getCurrentDuration(
        @DestinationVariable("meeting-id") Long meetingId) {
        MeetingCurrentDurationResponse response = meetingService.getCurrentDuration(meetingId);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @GetMapping("{meeting-id}/report")
    @Operation(summary = "회의 리포트 조회", description = "회의 리포트를 조회한다.")
    public ResponseEntity<ApiResult<MeetingReportResponse>> getMeetingReport(
        @PathVariable("meeting-id") Long meetingId) {
        MeetingReportInfoResponse meetingReportInfoResponse = meetingService.createReport(meetingId);

        MeetingReportResponse meetingReportResponse = new MeetingReportResponse(meetingReportInfoResponse);

        return ResponseEntity.ok(ApiUtils.success(meetingReportResponse));
    }

    @DeleteMapping("/{meeting-id}")
    @Operation(summary = "회의 삭제", description = "지정된 id에 해당하는 회의를 삭제한다.")
    public ResponseEntity deleteMeeting(@PathVariable("meeting-id") Long meetingId) {
        meetingService.cancelMeeting(meetingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{meeting-id}/users")
    @Operation(summary = "회의 참가자 조회", description = "회의에 참가한 사용자를 조회한다.")
    public ResponseEntity<ApiResult<MeetingMemberInfoResponse>> getMeetingMembers(
        @PathVariable("meeting-id") Long meetingId,
        @ReqUser Member member) {
        MeetingMemberInfoResponse response = meetingService.getMeetingMembers(meetingId, member.getId());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @DeleteMapping("/{meeting-id}/leave")
    @Operation(summary = "회의실 나가기", description = "지정된 id에 해당하는 회의에서 나간다.")
    public ResponseEntity leaveMeeting(@PathVariable("meeting-id") Long meetingId, @ReqUser Member member) {
        meetingService.leaveMeeting(meetingId, member.getId());

        return ResponseEntity.noContent().build();
    }
}
