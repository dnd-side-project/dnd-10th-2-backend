package org.dnd.timeet.oauth.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
import org.dnd.timeet.member.domain.MemberRole;
import org.dnd.timeet.oauth.OAuth2Provider;
import org.dnd.timeet.oauth.exception.OAuthProcessingException;
import org.dnd.timeet.oauth.info.OAuth2UserInfo;
import org.dnd.timeet.oauth.info.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {

        OAuth2Provider oauth2Provider = OAuth2Provider.valueOf(
            userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2Userinfo(oauth2Provider,
            oauth2User.getAttributes());

        if (userInfo.getId() == null) {
            throw new OAuthProcessingException("ID not found from OAuth2 provider");
        }

        Optional<Member> userOptional = memberRepository.findByOauthId(userInfo.getId());

        Member member;
        if (userOptional.isPresent()) {
            member = userOptional.get();
            if (oauth2Provider != member.getProvider()) {
                throw new OAuthProcessingException("Wrong Match Auth Provider");
            }

        } else {
            member = createUser(userInfo, oauth2Provider);
        }

        return new CustomUserDetails(member, oauth2User.getAttributes());
    }

    private Member createUser(OAuth2UserInfo userInfo, OAuth2Provider oauth2Provider) {
        return memberRepository.save(
            Member.builder()
                .name(userInfo.getName())
                .imageUrl(userInfo.getImageUrl())
                .role(MemberRole.ROLE_USER)
                .provider(oauth2Provider)
                .oauthId(userInfo.getId())
                .build()
        );
    }

}
