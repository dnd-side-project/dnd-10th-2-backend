package org.dnd.timeet.common.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.meeting.application.MeetingService;
import org.dnd.timeet.meeting.domain.Meeting;
import org.dnd.timeet.meeting.domain.MeetingRepository;
import org.dnd.timeet.meeting.dto.MeetingCreateRequest;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
import org.dnd.timeet.member.domain.MemberRole;
import org.dnd.timeet.oauth.OAuth2Provider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TestUtil {

    private final MemberRepository memberRepository;
    private final MeetingService meetingService;
    private final MeetingRepository meetingRepository;

    public TestUtil(MemberRepository memberRepository, MeetingService meetingService,
                    MeetingRepository meetingRepository) {
        this.memberRepository = memberRepository;
        this.meetingService = meetingService;
        this.meetingRepository = meetingRepository;
    }

    public void setMemberAuthentication(Member member) {
        UserDetails userDetails = new CustomUserDetails(member);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public Long createTestMeeting(Member hostMember) {
        MeetingCreateRequest request = MeetingCreateRequest.builder()
            .title("테스트 회의")
            .startTime(LocalDateTime.now().plusHours(1))
            .estimatedTotalDuration(LocalTime.of(1, 0, 0))
            .location("테스트 회의실")
            .description("테스트 설명")
            .imageNum(1)
            .build();
        Meeting meeting = meetingService.createMeeting(request, hostMember);
        return meetingRepository.save(meeting).getId();
    }

    public Member createTestMember() {
        return memberRepository.save(Member.builder()
            .role(MemberRole.ROLE_USER)
            .name("Test User")
            .imageUrl("http://example.com/image.jpg")
            .oauthId("oauth123")
            .provider(OAuth2Provider.KAKAO)
            .fcmToken("fcmToken123")
            .imageNum(5)
            .build());
    }
}
