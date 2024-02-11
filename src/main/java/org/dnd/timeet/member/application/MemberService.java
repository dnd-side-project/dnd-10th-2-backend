package org.dnd.timeet.member.application;


import java.util.Collections;
import java.util.Optional;
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

    public void upsertFcmToken(Long id, String fcmToken) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            member.get().setFcmToken(fcmToken);
        } else {
            throw new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MemberId", "Member not found"));
        }

    }
}