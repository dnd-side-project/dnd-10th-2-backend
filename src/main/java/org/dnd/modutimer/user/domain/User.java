package org.dnd.modutimer.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dnd.modutimer.user.application.AbstractJpaEntity;
import org.hibernate.annotations.Where;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user")
@Where(clause = "is_deleted=0")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends AbstractJpaEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private UserRole role;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false) // 암호화된 비밀번호가 저장됨 (255글자)
    private String password;

    // MEMO : 필수값들이므로 final 붙임
    @Builder
    public User(UserRole role, String name, String email, String password) {
        this.role = role;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
