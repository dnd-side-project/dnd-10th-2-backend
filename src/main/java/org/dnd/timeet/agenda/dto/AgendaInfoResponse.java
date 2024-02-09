//package org.dnd.timeet.agenda.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//import org.dnd.timeet.agenda.domain.Agenda;
//
//@Schema(description = "회의 정보 응답")
//@Getter
//@Setter
//public class AgendaInfoResponse {
//
//    @Schema(description = "회의 id", example = "12L")
//    private Long meetingId;
//
//    @Schema(description = "회의 제목", example = "2차 회의")
//    private String title;
//
//    @Schema(description = "회의 목", example = "2개의 사안 모두 해결하기")
//    private String description;
//
//    @Builder
//    public AgendaInfoResponse(Long meetingId, String title, String description) {
//        this.meetingId = meetingId;
//        this.title = title;
//        this.description = description;
//    }
//
//
//    public static AgendaInfoResponse from(Agenda meeting) { // 매개변수로부터 객체를 생성하는 팩토리 메서드
//        return AgendaInfoResponse.builder()
//            .meetingId(meeting.getId())
//            .title(meeting.getTitle())
//            .description(meeting.getDescription())
//            .build();
//    }
//}