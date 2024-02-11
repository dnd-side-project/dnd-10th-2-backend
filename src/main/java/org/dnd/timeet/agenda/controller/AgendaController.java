package org.dnd.timeet.agenda.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.application.AgendaService;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.dto.AgendaActionRequest;
import org.dnd.timeet.agenda.dto.AgendaActionResponse;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.agenda.dto.AgendaInfoResponse;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.common.utils.ApiUtils;
import org.dnd.timeet.common.utils.ApiUtils.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
        @RequestBody @Valid AgendaCreateRequest agendaCreateRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Agenda savedAgenda = agendaService.createAgenda(meetingId, agendaCreateRequest, userDetails.getMember());

        return ResponseEntity.ok(ApiUtils.success(savedAgenda.getId()));
    }

    @GetMapping("/{meeting-id}/agendas")
    @Operation(summary = "모든 안건 조회", description = "모든 안건을 조회한다.")
    public ResponseEntity getAgendas(
        @PathVariable("meeting-id") Long meetingId) {
        List<AgendaInfoResponse> agendaInfoResponseList = agendaService.findAll(meetingId)
            .stream()
            .map(AgendaInfoResponse::from)
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiUtils.success(agendaInfoResponseList));
    }

    /*
    * @MessageMapping : 클라이언트에서 해당 url로 메세지를 보내면 요청을 처리한다.
    * @SendTo : 브로커에게 메세지를 보낸다.
    *  과정
    *  1. 클라이언트에서 /app/meeting/{meetingId}/agendas/{agendaId}/action 으로 메세지를 보낸다.
    *  2. 핸들러가 메세지를 처리한다.
    *  3. 서버는 그 결과를 /topic/meeting/{meetingId}/agendas/{agendaId}/status 주소로 브로커에게 보낸다.
    *  4. 브로커는 해당 주소를 구독하고 있는 클라이언트에게 메세지를 전달한다.
     */
    @Operation(summary = "안건 제어 및 갱신", description = "해당 안건을 제어 및 갱신한다.")
    @MessageMapping("/meeting/{meeting-id}/agendas/{agenda-id}/action")
    @SendTo("/topic/meeting/{meeting-id}/agendas/{agenda-id}/status")
    public AgendaActionResponse handleAgendaAction(@DestinationVariable("meeting-id") Long meetingId,
                                                   @DestinationVariable("agenda-id") Long agendaId,
                                                   AgendaActionRequest actionRequest) {
        // 로직 구현 (안건 상태 변경)
        Agenda agenda = agendaService.changeAgendaStatus(meetingId, agendaId, actionRequest);
        // 변경된 안건 상태로 응답 객체 생성 및 반환
        return new AgendaActionResponse(agenda.getId(), agenda.getStatus());
    }
}
