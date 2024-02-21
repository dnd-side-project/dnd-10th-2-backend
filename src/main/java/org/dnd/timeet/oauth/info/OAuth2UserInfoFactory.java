package org.dnd.timeet.oauth.info;

import java.util.Map;
import org.dnd.timeet.oauth.OAuth2Provider;
import org.dnd.timeet.oauth.info.impl.GoogleOAuth2UserInfo;
import org.dnd.timeet.oauth.info.impl.KakaoOAuth2UserInfo;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static OAuth2UserInfo getOAuth2Userinfo(OAuth2Provider oauth2Provider,
                                                   Map<String, Object> attributes) {

        if (oauth2Provider == OAuth2Provider.GOOGLE) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        if (oauth2Provider == OAuth2Provider.NAVER) {
            return null;
        }
        if (oauth2Provider == OAuth2Provider.KAKAO) {
            return new KakaoOAuth2UserInfo(attributes);
        }
        throw new IllegalArgumentException("Invalid AuthProvider Type.");
    }
}
