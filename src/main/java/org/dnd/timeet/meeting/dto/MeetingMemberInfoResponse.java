package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.member.domain.Member;

@Schema(description = "회의 멤버 정보 응답")
@Getter
@Setter
public class MeetingMemberInfoResponse {

    MeetingMemberDetailResponse hostMember;
    List<MeetingMemberDetailResponse> members;

    public MeetingMemberInfoResponse(MeetingMemberDetailResponse hostMember,
                                     List<MeetingMemberDetailResponse> members) {
        this.hostMember = hostMember;
        this.members = members;
    }

    @Getter
    @Setter
    public static class MeetingMemberDetailResponse {

        @Schema(description = "사용자 id", nullable = false, example = "12")
        private Long id;
        @Schema(description = "사용자 이름", nullable = false, example = "green12")
        private String nickname;


        public MeetingMemberDetailResponse(Member member) {
            this.id = member == null ? null : member.getId();
            this.nickname = member == null ? null : member.getName();
        }
    }
}


