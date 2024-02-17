package org.dnd.timeet.timer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.timer.application.TimerService;
import org.dnd.timeet.timer.domain.Timer;

import org.dnd.timeet.timer.dto.TimerCreateRequest;
import org.dnd.timeet.timer.dto.TimerInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Timer 컨트롤러", description = "Timer API입니다.")
@RestController
@RequestMapping("/api/v1/timers")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    @GetMapping
    @Operation(summary = "모든 타이머 조회", description = "모든 타이머를 조회한다.")
    public ResponseEntity getTimers() {
        // TODO : 해당 User의 타이머만 조회하도록 수정
        List<TimerInfoResponse> timerInfoResponseList = timerService.findAll()
            .stream()
            .map(TimerInfoResponse::from)
            .collect(Collectors.toList());

        return ResponseEntity.ok().body(timerInfoResponseList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "단일 타이머 조회", description = "지정된 ID에 해당하는 타이머를 조회한다.")
    public ResponseEntity<TimerInfoResponse> getTimerById(@PathVariable("id") Long timerId) {
        Timer timer = timerService.findById(timerId);
        TimerInfoResponse timerInfoResponse = TimerInfoResponse.from(timer);

        return ResponseEntity.ok(timerInfoResponse);
    }

    @PostMapping
    @Operation(summary = "타이머 생성", description = "타이머를 생성한다.")
    public ResponseEntity<Void> createTimer(@RequestBody @Valid TimerCreateRequest timerCreateRequest) {
        // TODO : 세션 키 처리부분 추가

        Timer savedTimer = timerService.createTimer(timerCreateRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedTimer.getId())
            .toUri(); // http://localhost:8080/api/v1/timers/123

        return ResponseEntity.created(location).build(); // 201(Created) 상태코드 + URI 반환
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "타이머 삭제", description = "지정된 ID에 해당하는 타이머를 삭제한다.")
    public ResponseEntity deleteTimer(@PathVariable("id") Long timerId) {
        timerService.deleteTimer(timerId);

        return ResponseEntity.noContent().build(); // 204(No Content)
    }
}
