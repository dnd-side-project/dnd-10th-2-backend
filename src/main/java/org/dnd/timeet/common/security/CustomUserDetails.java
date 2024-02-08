package org.dnd.timeet.common.security;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.dnd.timeet.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Member member;
    private transient Map<String, Object> atrributes;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.atrributes = attributes;
    }

    //// UserDetail Override
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = member.getRole().name();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(member.getId());
    }

    public Long getId() {
        return member.getId();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User Override
    @Override
    public String getName() {
        return String.valueOf(member.getId());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return atrributes;
    }
}
