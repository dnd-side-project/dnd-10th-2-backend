package org.dnd.timeet.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponse {

    private String jwtToken;
    private String redirectUrl;

    public UserLoginResponse(String jwtToken, String redirectUrl) {
        this.jwtToken = jwtToken;
        this.redirectUrl = redirectUrl;
    }
}
