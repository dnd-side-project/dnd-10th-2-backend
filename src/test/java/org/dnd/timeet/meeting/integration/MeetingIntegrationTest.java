package org.dnd.timeet.meeting.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.dnd.timeet.common.security.annotation.WithMockCustomUser;
import org.dnd.timeet.meeting.application.MeetingService;
import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("[API][Integration] 회의 API 테스트")
class MeetingIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    MeetingService meetingService;

    @Test
    @WithMockUser(username = "1", roles = "USER")
    @DisplayName("[GET] 타이머 조회 API 테스트")
    void getTimers() throws Exception {


        ResultActions perform = mvc.perform(
            get("/api/v1/timers")
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform
            .andExpect(status().isOk()) // 201 Created 상태 코드가 반환되는지 확인
            .andDo(print()); // 요청/응답 로그를 출력합니다.

    }

    @Test
    @WithMockCustomUser
    @DisplayName("[POST] 회의 생성 API 테스트")
    void createMeeting() throws Exception {
        // given
        MeetingCreateRequest meetingCreateRequest = MeetingCreateRequest.builder()
            .title("Test Meeting")
            .location("Test Location")
            .startTime(LocalDateTime.now())
            .description("Test Description")
            .estimatedTotalDuration(LocalTime.of(3, 20, 0))
            .imageNum(5)
            .build();
        String requestBody = om.writeValueAsString(meetingCreateRequest);

        // when
        ResultActions perform = mvc.perform(
            post("/api/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[POST] 회의 참가 API 테스트")
    void attendMeeting() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            post("/api/meetings/2/attend")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[PATCH] 회의 종료 API 테스트 : 실패 - 방장이 아닐 경우")
    void closeMeeting() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            patch("/api/meetings/2/end")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isForbidden())
            .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[GET] 단일 회의 조회 API 테스트")
    void getTimerById() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            get("/api/meetings/2")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.success").value(true));
        // MEMO : 테스트 DB에 따라 반환되는 값이 달라질 수 있음
//                .andExpect(jsonPath("$.response.description").value("2개의 사안 모두 해결하기"))
//                .andExpect(jsonPath("$.response.meetingStatus").value("COMPLETED"))
//                .andExpect(jsonPath("$.response.hostMemberId").value(1))
//                .andExpect(jsonPath("$.response.startTime").value("2024-02-28T07:33:01"))
//                .andExpect(jsonPath("$.response.totalEstimatedDuration").value("02:00:00"))
//                .andExpect(jsonPath("$.response.imgNum").value(1));
    }

    // TODO : 웹소켓 테스트
    @Test
    void getCurrentDuration() {

    }

    @Test
    @WithMockCustomUser
    @DisplayName("[GET] 리포트 조회 API 테스트")
    void getMeetingReport() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            get("/api/meetings/2/report")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[DELETE] 회의 삭제 API 테스트")
    void deleteMeeting() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            delete("/api/meetings/2")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isNoContent()) // 204 No Content
            .andDo(print());
    }

    // MEMO: JWT 토큰 정보값 문제 때문에 테스트 불가
//    @Test
//    @WithMockCustomUser
//    @DisplayName("[GET] 회의 참가자 조회 API 테스트")
//    void getMeetingMembers() throws Exception {
//        // given
//
//        // when
//        ResultActions perform = mvc.perform(
//            get("/api/meetings/4/users")
//                .contentType(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        perform
//            .andExpect(status().isOk())
//            .andDo(print());
//    }

    // MEMO: 테스트 환경에서 JWT 토큰을 통한 인증을 시뮬레이션하기 위해 Member 객체를 생성하고 있다.
    // 이 과정에서 Member 객체는 ID 없이 생성되는데, 테스트 중에 Member 객체의 ID가 필요한 경우가 있어 에러가 발생한다.
//    @Test
//    @WithMockCustomUser
//    @DisplayName("[DELETE] 회의 나가기 API 테스트")
//    void leaveMeeting() throws Exception {
//        // given
//
//        // when
//        ResultActions perform = mvc.perform(
//            delete("/api/meetings/2/leave")
//                .contentType(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        perform
//            .andExpect(status().isNoContent()) // 204 No Content
//            .andDo(print());
//    }
}