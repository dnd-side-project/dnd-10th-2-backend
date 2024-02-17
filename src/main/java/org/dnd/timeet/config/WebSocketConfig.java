package org.dnd.timeet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 메시지 브로커가 /topic으로 시작하는 메시지를 클라이언트로 브로드캐스팅 (1:N 통신)
        config.setApplicationDestinationPrefixes("/app"); // 핸들러 메소드가 /app으로 시작하는 메시지를 처리
    }

    // 웹소켓 연결을 위한 endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*"); // 모든 도메인에서 접근 허용
//            .withSockJS(); // /ws로 접속하면 SockJS를 통해 웹소켓 연결
    }
}

