package org.dnd.timeet.agenda.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.agenda.application.AgendaService;
import org.dnd.timeet.agenda.domain.Agenda;
import org.dnd.timeet.agenda.dto.AgendaCreateRequest;
import org.dnd.timeet.agenda.dto.AgendaInfoResponse;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.common.utils.ApiUtils;
import org.dnd.timeet.common.utils.ApiUtils.ApiResult;
import org.springframework.http.ResponseEntity;
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
        Agenda savedMeeting = agendaService.createAgenda(meetingId, agendaCreateRequest, userDetails.getMember());

        return ResponseEntity.ok(ApiUtils.success(savedMeeting.getId()));
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

}
