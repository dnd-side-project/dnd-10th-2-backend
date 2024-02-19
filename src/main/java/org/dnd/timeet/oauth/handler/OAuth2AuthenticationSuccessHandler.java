package org.dnd.timeet.oauth.handler;

import static org.dnd.timeet.common.security.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dnd.timeet.common.exception.BadRequestError;
import org.dnd.timeet.common.exception.BadRequestError.ErrorCode;
import org.dnd.timeet.common.security.CookieAuthorizationRequestRepository;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.common.security.JwtProvider;
import org.dnd.timeet.common.utils.CookieUtil;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.auth.oauth2.authorized-redirect-uris}")
    private String[] AUTHORIZED_REDIRECT_URIS;

    private final CookieAuthorizationRequestRepository authorizationRequestRepository;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirect = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue);

        if (redirect.isPresent() && !isAuthorizedRedirectUri(redirect.get())) {
            throw new BadRequestError(ErrorCode.VALIDATION_FAILED,
                Collections.singletonMap("redirect", "Unauthorized redirect uri"));
        }
        String targetUrl = redirect.orElse(getDefaultTargetUrl());

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Optional<Member> user = memberRepository.findById(userDetails.getId());

        // jwt

        String accessToken = JwtProvider.create(user.get());

        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("code", accessToken)
            .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request,
                                                 HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        for (String authorizedRedirectUri : AUTHORIZED_REDIRECT_URIS) {
            URI authorizedUri = URI.create(authorizedRedirectUri);
            if (authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort()) {
                return true;
            }
        }
        return false;
    }
}