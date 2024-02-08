package org.dnd.timeet.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRole;

@Getter
@Setter
public class MemberInfoResponse {

    @Schema(description = "사용자 id", nullable = false, example = "12")
    private long id;
    @Schema(description = "사용자 이름", nullable = false, example = "green12")
    private String username;
    @Schema(description = "사용자 역할", nullable = false, example = "ROLE_USER")
    private MemberRole role;


    public MemberInfoResponse(long id, String username, MemberRole role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
            member.getId(),
            member.getName(),
            member.getRole()
        );
    }
}
