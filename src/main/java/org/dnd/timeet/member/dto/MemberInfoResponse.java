package org.dnd.timeet.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.member.domain.Member;

@Getter
@Setter
public class MemberInfoResponse {

    @Schema(description = "사용자 id", nullable = false, example = "12")
    private long id;
    @Schema(description = "사용자 이름", nullable = false, example = "green12")
    private String name;


    public MemberInfoResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
            member.getId(),
            member.getName()
        );
    }
}
