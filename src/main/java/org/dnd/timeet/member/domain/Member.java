package org.dnd.timeet.member.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.BaseEntity;
import org.dnd.timeet.oauth.OAuth2Provider;
import org.dnd.timeet.participant.domain.Participant;
import org.hibernate.annotations.Where;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "member")
@Where(clause = "is_deleted=false")
@AttributeOverride(name = "id", column = @Column(name = "member_id"))
public class Member extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private MemberRole role;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 255, nullable = false, name = "image_url")
    private String imageUrl;

    @Column(length = 100, nullable = false, name = "oauth_id")
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private OAuth2Provider provider;

    @Column(length = 255)
    private String fcmToken;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private Set<Participant> participations = new HashSet<>();

    @Column(nullable = false, name = "image_num")
    private Integer imageNum = new Random().nextInt(12) + 1;

    @Builder
    public Member(MemberRole role, String name, String imageUrl, String oauthId, OAuth2Provider provider,
                  String fcmToken, Integer imageNum) {
        this.role = role;
        this.name = name;
        this.imageUrl = imageUrl;
        this.oauthId = oauthId;
        this.provider = provider;
        this.fcmToken = fcmToken;
        this.imageNum = imageNum;
    }


    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
