package org.dnd.timeet.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.security.annotation.ReqUser;
import org.dnd.timeet.common.utils.ApiUtils;
import org.dnd.timeet.common.utils.ApiUtils.ApiResult;
import org.dnd.timeet.member.application.MemberService;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.dto.MemberNicknameRequest;
import org.dnd.timeet.member.dto.RegisterFcmRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member 컨트롤러", description = "Member API입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PatchMapping
    @Operation(summary = "fcmToken 등록", description = "fcmToken을 등록한다.")
    public void registerFcmToken(@RequestBody RegisterFcmRequest registerFcmRequest,
                                 @ReqUser Member member) {
        memberService.upsertFcmToken(member.getId(), registerFcmRequest.getFcmToken());

    }

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 추가", description = "닉네임을 추가한다.")
    public ResponseEntity<ApiResult<String>> registerNickname(
        @RequestBody MemberNicknameRequest memberNicknameRequest,
        @ReqUser Member member) {
        String updatedNickname = memberService.updateNickname(member.getId(),
            memberNicknameRequest.getNickname());
        return ResponseEntity.ok(ApiUtils.success(updatedNickname));
    }
}

