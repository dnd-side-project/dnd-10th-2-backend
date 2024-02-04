package org.dnd.modutimer.common.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.dnd.modutimer.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTProvider {

    public static final Long EXP = 1000L * 60 * 60 * 48; // 48시간
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String SECRET = "MySecretKey";

    public static String create(User user) {
        String jwt = JWT.create()
            .withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
            .withClaim("id", user.getId())
            .withClaim("role", user.getRole().name())
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
