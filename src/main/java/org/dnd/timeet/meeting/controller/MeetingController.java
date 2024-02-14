package org.dnd.timeet.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.common.utils.ApiUtils;
import org.dnd.timeet.common.utils.ApiUtils.ApiResult;
import org.dnd.timeet.meeting.application.MeetingService;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.dnd.timeet.meeting.dto.MeetingCreateResponse;
import org.dnd.timeet.meeting.dto.MeetingInfoResponse;
import org.dnd.timeet.member.dto.MemberInfoListResponse;
import org.dnd.timeet.member.dto.MemberInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/{id}")
    @Operation(summary = "단일 회의 조회", description = "지정된 id에 해당하는 회의를 조회한다.")
    public ResponseEntity<ApiResult<MeetingInfoResponse>> getTimerById(@PathVariable("id") Long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        MeetingInfoResponse meetingInfoResponse = MeetingInfoResponse.from(meeting);

        return ResponseEntity.ok(ApiUtils.success(meetingInfoResponse));
    }

    @GetMapping("/{meeting-id}/users")
    @Operation(summary = "회의 참가자 조회", description = "회의에 참가한 사용자를 조회한다.")
    public ResponseEntity<ApiResult<MemberInfoListResponse>> getMeetingMembers(
        @PathVariable("meeting-id") Long meetingId) {
        List<MemberInfoResponse> memberInfoList = meetingService.getMeetingMembers(meetingId)
            .stream()
            .map(MemberInfoResponse::from)
            .collect(Collectors.toList());

        MemberInfoListResponse memberInfoListResponse = new MemberInfoListResponse(memberInfoList);

        return ResponseEntity.ok(ApiUtils.success(memberInfoListResponse));
    }


}
