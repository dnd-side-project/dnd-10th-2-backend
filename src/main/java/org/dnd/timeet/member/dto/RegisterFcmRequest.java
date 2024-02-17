package org.dnd.timeet.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "fcmToken 등록 요청")
@Getter
@Setter
@NoArgsConstructor
public class RegisterFcmRequest {

    @Schema(description = "fcmToken", nullable = false, example = "fcmToken")
    private String fcmToken;
}
