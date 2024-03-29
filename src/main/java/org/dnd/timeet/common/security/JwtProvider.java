package org.dnd.timeet.common.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.dnd.timeet.member.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    public static final Long EXP = 1000L * 60 * 60 * 48; // 48시간
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";


    public static String SECRET;

    @Value("${app.auth.token.secret-key}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    public static String create(Member member) {
        String jwt = JWT.create()
            .withSubject(String.valueOf(member.getId()))
            .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
            .withClaim("id", member.getId())
            .withClaim("role", member.getRole().name())
            .sign(Algorithm.HMAC512(SECRET));

        return TOKEN_PREFIX + jwt;
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        jwt = jwt.replace(TOKEN_PREFIX, "");
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
            .build().verify(jwt);

        return decodedJWT;
    }

}
