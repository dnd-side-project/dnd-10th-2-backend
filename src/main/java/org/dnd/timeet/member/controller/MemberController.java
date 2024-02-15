package org.dnd.timeet.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.security.annotation.ReqUser;
import org.dnd.timeet.member.application.MemberService;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.dto.RegisterFcmRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member 컨트롤러", description = "Member API입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PatchMapping
    @Operation(summary = "fcmToken 등록", description = "fcmToken을 등록한다.")
    public void registerFcmToken(@RequestBody RegisterFcmRequest registerFcmRequest,
                                 @ReqUser Member member) {
        memberService.upsertFcmToken(member.getId(), registerFcmRequest.getFcmToken());
        
    }
}

