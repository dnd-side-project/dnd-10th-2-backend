package org.dnd.timeet.member.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.timeet.common.domain.BaseEntity;
import org.dnd.timeet.oauth.OAuth2Provider;
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

    @Column(length = 255, nullable = false)
    private String imageUrl;

    @Column(length = 100, nullable = false)
    private Long oauthId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private OAuth2Provider provider;

    // MEMO : 필수값들이므로 final 붙임
    @Builder
    public Member(MemberRole role, String name, String imageUrl, Long oauthId, OAuth2Provider provider) {
        this.role = role;
        this.name = name;
        this.imageUrl = imageUrl;
        this.oauthId = oauthId;
        this.provider = provider;
    }
}
