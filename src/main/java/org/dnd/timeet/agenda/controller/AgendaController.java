package org.dnd.timeet.agenda.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.application.AgendaService;
import org.dnd.timeet.agenda.dto.AgendaActionRequest;
import org.dnd.timeet.agenda.dto.AgendaActionResponse;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.agenda.dto.AgendaInfoResponse;
import org.dnd.timeet.agenda.dto.AgendaOrderRequest;
import org.dnd.timeet.agenda.dto.AgendaPatchRequest;
import org.dnd.timeet.agenda.dto.AgendaPatchResponse;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.common.utils.ApiUtils;
import org.dnd.timeet.common.utils.ApiUtils.ApiResult;
import org.dnd.timeet.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "안건 컨트롤러", description = "Agenda API입니다.")
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @Operation(summary = "안건(+쉬는시간) 생성", description = "안건(+쉬는시간)을 생성한다.")
    @MessageMapping("/meeting/{meeting-id}/agendas/create")
    @SendTo("/topic/meeting/{meeting-id}/agendas/create")
    public ResponseEntity<ApiResult<Long>> createAgenda(
            @DestinationVariable("meeting-id") Long meetingId,
            @Valid AgendaCreateRequest agendaCreateRequest,
            Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // userDetails 객체를 사용하여 작업 수행
            Long agendaId = agendaService.createAgenda(meetingId, agendaCreateRequest, userDetails.getMember());
            return ResponseEntity.ok(ApiUtils.success(agendaId));
        }

        // 인증 정보가 없을 때
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{meeting-id}/agendas")
    @Operation(summary = "모든 안건 조회", description = "모든 안건을 조회한다.")
    public ResponseEntity<ApiResult<AgendaInfoResponse>> getAgendas(
            @PathVariable("meeting-id") Long meetingId) {
        AgendaInfoResponse response = agendaService.findAgendas(meetingId);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     * @MessageMapping : 클라이언트에서 해당 url로 메세지를 보내면 요청을 처리한다.
     * @SendTo : 브로커에게 메세지를 보낸다.
     *  과정
     *  1. 클라이언트에서 /app/meeting/{meeting-id}/agendas/{agenda-id}/action 으로 메세지를 보낸다.
     *  2. 핸들러가 메세지를 처리한다.
     *  3. 서버는 그 결과를 /topic/meeting/{meeting-id}/agendas/{agenda-id}/status 주소로 브로커에게 보낸다.
     *  4. 브로커는 해당 주소를 구독하고 있는 클라이언트에게 메세지를 전달한다.
     */
    @Operation(summary = "안건 제어 및 갱신", description = "해당 안건을 제어 및 갱신한다.")
    @MessageMapping("/meeting/{meeting-id}/agendas/{agenda-id}/action")
    @SendTo("/topic/meeting/{meeting-id}/agendas/{agenda-id}/status")
    public AgendaActionResponse handleAgendaAction(@DestinationVariable("meeting-id") Long meetingId,
                                                   @DestinationVariable("agenda-id") Long agendaId,
                                                   AgendaActionRequest actionRequest) {
        // 로직 구현 (안건 상태 변경)
        return agendaService.changeAgendaStatus(meetingId, agendaId, actionRequest);
    }

    @Operation(summary = "안건 삭제", description = "지정된 ID에 해당하는 안건을 삭제한다.")
    @MessageMapping("/meeting/{meeting-id}/agendas/{agenda-id}/delete")
    @SendTo("/topic/meeting/{meeting-id}/delete/{agenda-id}/delete")
    public ResponseEntity deleteAgenda(
            @DestinationVariable("meeting-id") Long meetingId,
            @DestinationVariable("agenda-id") Long agendaId) {
        agendaService.cancelAgenda(meetingId, agendaId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "안건 순서 변경", description = "안건의 순서를 변경한다.")
    @MessageMapping("/meeting/{meeting-id}/agendas/order")
    @SendTo("/topic/meeting/{meeting-id}/agendas/order")
    public ResponseEntity<ApiResult<AgendaInfoResponse>> changeAgendaOrder(
            @DestinationVariable("meeting-id") Long meetingId,
            @Valid AgendaOrderRequest agendaOrderRequest) {
        AgendaInfoResponse agendaInfoResponse = agendaService.changeAgendaOrder(meetingId,
                agendaOrderRequest.getAgendaIds());
        return ResponseEntity.ok(ApiUtils.success(agendaInfoResponse));
    }

    @PatchMapping("/{meeting-id}/agendas/{agenda-id}")
    @Operation(summary = "안건 수정", description = "지정된 ID에 해당하는 안건을 수정한다.")
    public ResponseEntity<ApiResult<AgendaPatchResponse>> deleteAgenda(
            @PathVariable("meeting-id") Long meetingId,
            @PathVariable("agenda-id") Long agendaId,
            @RequestBody AgendaPatchRequest patchRequest) {
        AgendaPatchResponse response = agendaService.patchAgenda(meetingId, agendaId, patchRequest);

        return ResponseEntity.ok(ApiUtils.success(response));
    }
}
