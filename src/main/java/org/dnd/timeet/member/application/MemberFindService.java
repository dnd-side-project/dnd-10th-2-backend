package org.dnd.timeet.member.application;

import java.util.Collections;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberFindService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberFindService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getUserById(Long id) throws Exception {
        return memberRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("User", "User not found")));
    }
}