package org.dnd.timeet.agenda.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.application.AgendaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "안건 컨트롤러", description = "Agenda API입니다.")
@RestController
@RequestMapping("/api/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

//    @PostMapping
//    @Operation(summary = "회의 생성", description = "회의를 생성한다.")
//    public ResponseEntity<ApiResult<MeetingCreateResponse>> createMeeting(
//        @RequestBody @Valid MeetingCreateRequest meetingCreateRequest) {
//        // TODO : 유저 인증 로직 추가
//        Agenda savedMeeting = meetingService.createMeeting(meetingCreateRequest);
//        MeetingCreateResponse meetingCreateResponse = MeetingCreateResponse.from(savedMeeting);
//
//        return ResponseEntity.ok(ApiUtils.success(meetingCreateResponse));
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "단일 회의 조회", description = "지정된 id에 해당하는 회의를 조회한다.")
//    public ResponseEntity<ApiResult<MeetingInfoResponse>> getTimerById(@PathVariable("id") Long meetingId) {
//        Agenda meeting = meetingService.findById(meetingId);
//        MeetingInfoResponse meetingInfoResponse = MeetingInfoResponse.from(meeting);
//
//        return ResponseEntity.ok(ApiUtils.success(meetingInfoResponse));
//    }

}
