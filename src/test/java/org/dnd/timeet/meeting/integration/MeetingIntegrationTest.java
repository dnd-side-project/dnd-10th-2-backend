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
import org.dnd.timeet.common.utils.TestUtil;
import org.dnd.timeet.meeting.application.MeetingService;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.dnd.timeet.meeting.domain.MeetingStatus;
import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("[API][Integration] 회의 API 테스트")
public class MeetingIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    MeetingService meetingService;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MemberRepository memberRepository;

    private Long meetingId;

    private Long memberId;

    @BeforeEach
    void setup() {
        TestUtil testUtil = new TestUtil(memberRepository, meetingService, meetingRepository);
        Member member = testUtil.createTestMember();
        testUtil.setMemberAuthentication(member);

        meetingId = testUtil.createTestMeeting(member);
        memberId = member.getId();
    }

    @AfterEach
    void cleanup() {
        if (meetingId != null) {
            meetingRepository.deleteById(meetingId);
        }
        if (memberId != null) {
            memberRepository.deleteById(memberId);
        }
        SecurityContextHolder.clearContext();
    }

    @Test
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
    @DisplayName("[POST] 회의 참가 API 테스트 - 실패")
    void attendMeeting() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            post("/api/meetings/" + meetingId + "/attend")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    @DisplayName("[PATCH] 회의 종료 API 테스트")
    void closeMeeting() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            patch("/api/meetings/" + meetingId + "/end")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("[GET] 단일 회의 조회 API 테스트")
    void getTimerById() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            get("/api/meetings/" + meetingId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.response.meetingId").value(meetingId))
            .andExpect(jsonPath("$.response.hostMemberId").value(memberId))
            .andExpect(jsonPath("$.response.title").value("테스트 회의"))
            .andExpect(jsonPath("$.response.meetingStatus").value(MeetingStatus.SCHEDULED.name()))
            .andExpect(jsonPath("$.response.description").value("테스트 설명"))
            .andExpect(jsonPath("$.response.imgNum").value(1));
    }

    // TODO : 웹소켓 테스트
    @Test
    void getCurrentDuration() {

    }

    @Test
    @DisplayName("[GET] 리포트 조회 API 테스트")
    void getMeetingReport() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            get("/api/meetings/" + meetingId + "/report")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("[DELETE] 회의 삭제 API 테스트")
    void deleteMeeting() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            delete("/api/meetings/" + meetingId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isNoContent()) // 204 No Content
            .andDo(print());
    }

    @Test
    @DisplayName("[GET] 회의 참가자 조회 API 테스트")
    void getMeetingMembers() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            get("/api/meetings/" + meetingId + "/users")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isOk())
            .andDo(print());
    }

    // MEMO: 테스트 환경에서 JWT 토큰을 통한 인증을 시뮬레이션하기 위해 Member 객체를 생성하고 있다.
    // 이 과정에서 Member 객체는 ID 없이 생성되는데, 테스트 중에 Member 객체의 ID가 필요한 경우가 있어 에러가 발생한다.
    @Test
    @DisplayName("[DELETE] 회의 나가기 API 테스트")
    void leaveMeeting() throws Exception {
        // given

        // when
        ResultActions perform = mvc.perform(
            delete("/api/meetings/" + meetingId + "/leave")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
            .andExpect(status().isNoContent()) // 204 No Content
            .andDo(print());
    }
}