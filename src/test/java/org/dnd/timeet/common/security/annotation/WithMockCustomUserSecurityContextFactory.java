package org.dnd.timeet.common.security.annotation;

import java.util.HashSet;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRole;
import org.dnd.timeet.oauth.OAuth2Provider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = Member.builder()
            .role(MemberRole.ROLE_USER)
            .name("Test User")
            .imageUrl("http://example.com/image.jpg")
            .oauthId("oauth123")
            .provider(OAuth2Provider.KAKAO)
            .fcmToken("fcmToken123")
            .imageNum(5)
            .participations(new HashSet<>())
            .build();

        CustomUserDetails userDetails = new CustomUserDetails(member);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(auth);

        return context;
    }
}
