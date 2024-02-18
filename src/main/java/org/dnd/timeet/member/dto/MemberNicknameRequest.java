package org.dnd.timeet.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "nickname 등록 요청")
@Getter
@Setter
@NoArgsConstructor
public class MemberNicknameRequest {

    @Schema(description = "nickname", nullable = false, example = "greenfrog")
    private String nickname;
}
