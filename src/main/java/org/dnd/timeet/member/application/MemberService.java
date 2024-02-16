package org.dnd.timeet.member.application;


import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public void upsertFcmToken(Long id, String fcmToken) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MemberId", "Member not found")));
        member.setFcmToken(fcmToken);


    }
}