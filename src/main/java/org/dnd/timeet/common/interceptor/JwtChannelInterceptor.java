package org.dnd.timeet.common.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.firebase.database.annotations.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dnd.timeet.common.security.CustomUserDetails;
import org.dnd.timeet.common.security.JwtProvider;
import org.dnd.timeet.meeting.application.WebSocketSessionManager;
import org.dnd.timeet.member.application.MemberFindService;
import org.dnd.timeet.member.domain.Member;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * WebSocket 채널에 JWT 검증하는 인터셉터
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final MemberFindService userUtilityService;
    private final WebSocketSessionManager sessionManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        // 연결 요청 시 JWT 검증 및 인증 정보 설정
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader(JwtProvider.HEADER);
            if (authorization != null && !authorization.isEmpty()) {
                String jwt = authorization.get(0).substring(JwtProvider.TOKEN_PREFIX.length());
                try {
                    // JWT 토큰 검증
                    DecodedJWT decodedJWT = JwtProvider.verify(jwt);
                    Long memberId = decodedJWT.getClaim("id").asLong();

                    // 사용자 정보 조회
                    Member member = userUtilityService.getUserById(memberId);

                    // 사용자 인증 정보 생성
                    CustomUserDetails userDetails = new CustomUserDetails(member);
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    // 세션 매니저에 사용자 세션 추가

                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String sessionId = accessor.getSessionId();
                    sessionManager.addUserSession(sessionId, memberId);
                    log.info("User Added. Active User Count: " + sessionManager.getActiveUserCount());
                } catch (JWTVerificationException e) {
                    log.error("JWT Verification Failed: " + e.getMessage());
                    return null;
                } catch (Exception e) {
                    log.error("An unexpected error occurred: " + e.getMessage());
                    return null;
                }
            } else {
                log.error("Authorization header is not found");
                return null;
            }
        }
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 연결 해제 시 세션 정보 제거
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            sessionManager.removeUserSession(sessionId);
            log.info("User Disconnected. Active User Count: " + sessionManager.getActiveUserCount());

            // SecurityContextHolder의 컨텍스트 제거
            SecurityContextHolder.clearContext();
        }
    }
}

