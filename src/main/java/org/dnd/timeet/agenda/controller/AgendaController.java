package org.dnd.timeet.agenda.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.application.AgendaService;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.common.utils.ApiUtils;
import org.dnd.timeet.common.utils.ApiUtils.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "안건 컨트롤러", description = "Agenda API입니다.")
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping("/{meeting-id}/agendas")
    @Operation(summary = "안건(+쉬는시간) 생성", description = "안건(+쉬는시간)을 생성한다.")
    public ResponseEntity<ApiResult<Long>> createMeeting(
        @PathVariable("meeting-id") Long meetingId,
        @RequestBody @Valid AgendaCreateRequest agendaCreateRequest) {
        // TODO : 유저 인증 로직 추가
        Agenda savedMeeting = agendaService.createAgenda(meetingId, agendaCreateRequest);

        return ResponseEntity.ok(ApiUtils.success(savedMeeting.getId()));
    }
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
