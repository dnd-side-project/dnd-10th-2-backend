package org.dnd.timeet.fcm.application;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.dnd.timeet.common.exception.InternalServerError;
import org.dnd.timeet.common.exception.NotFoundError;
import org.dnd.timeet.fcm.domain.FCMNotificationRequestDto;
import org.dnd.timeet.member.domain.Member;
import org.dnd.timeet.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FCMNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;

    public void sendNotificationByToken(FCMNotificationRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.getTargetMemberId())
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("MemberId", "Member not found")));
        if (member.getFcmToken() == null) {
            throw new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("fcmToken", "fcmToken not exist"));
        }

        Notification notification = Notification.builder()
            .setTitle(requestDto.getTitle())
            .setBody(requestDto.getBody())
            .build();

        Message message = Message.builder()
            .setToken(member.getFcmToken())
            .setNotification(notification)
            .build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            throw new InternalServerError(InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR,
                Collections.singletonMap("fcmSend", "Fail to send fcm"));
        }


    }
}
