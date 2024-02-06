package org.dnd.timeet.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dnd.timeet.user.domain.User;
import org.dnd.timeet.user.domain.UserRole;

@Getter
@Setter
public class UserInfoResponse {

    @Schema(description = "사용자 id", nullable = false, example = "12")
    private long id;
    @Schema(description = "사용자 이름", nullable = false, example = "green12")
    private String username;
    @Schema(description = "사용자 이메일", nullable = false, example = "green12@gmail.com")
    private String email;
    @Schema(description = "사용자 역할", nullable = false, example = "ROLE_USER")
    private UserRole role;


    public UserInfoResponse(long id, String username, String email, UserRole role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
