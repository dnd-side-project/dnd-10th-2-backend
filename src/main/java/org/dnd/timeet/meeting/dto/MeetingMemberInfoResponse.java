package org.dnd.timeet.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.member.domain.Member;

@Schema(description = "회의 참가자 정보 응답")
@Getter
@Setter
public class MeetingMemberInfoResponse {

    @Schema(description = "방장 제외 참가자 정보")
    List<MeetingMemberDetailResponse> members;

    @Schema(description = "방장 정보")
    MeetingMemberDetailResponse hostMember;

    @Schema(description = "방장 여부", example = "true")
    boolean isHost;


    public MeetingMemberInfoResponse(List<MeetingMemberDetailResponse> members,
                                     MeetingMemberDetailResponse hostMember,
                                     boolean isHost) {
        this.members = members;
        this.hostMember = hostMember;
        this.isHost = isHost;
    }

    @Getter
    @Setter
    public static class MeetingMemberDetailResponse {

        @Schema(description = "사용자 id", nullable = false, example = "12")
        private Long id;

        @Schema(description = "사용자 이름", nullable = false, example = "green12")
        private String nickname;

        @Schema(description = "이미지 번호(1~12 사이 랜덤)", nullable = false, example = "2")
        private Long imageNum;


        public MeetingMemberDetailResponse(Member member) {
            this.id = member == null ? null : member.getId();
            this.nickname = member == null ? null : member.getName();
            this.imageNum = new Random().nextLong(12) + 1;
        }
    }
}


