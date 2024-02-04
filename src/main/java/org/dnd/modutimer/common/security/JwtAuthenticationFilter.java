package org.dnd.modutimer.common.security;


import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dnd.modutimer.common.exception.ApiException;
import org.dnd.modutimer.common.exception.BadRequestError;
import org.dnd.modutimer.common.exception.InternalServerError;
import org.dnd.modutimer.common.exception.UnAuthorizedError;
import org.dnd.modutimer.user.domain.User;
import org.dnd.modutimer.common.utils.ApiUtils;
import org.dnd.modutimer.user.application.UserFindService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collections;

import static org.dnd.modutimer.config.SecurityConfig.PUBLIC_URLS;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final UserFindService userUtilityService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserFindService userUtilityService) {
        super(authenticationManager);
        this.userUtilityService = userUtilityService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(JWTProvider.HEADER);

        try {
            if (jwt != null && !isNonProtectedUrl(request)) { // 토큰이 있고 보호된 URL일 경우 토큰 검증
                DecodedJWT decodedJWT = JWTProvider.verify(jwt);
                Long id = decodedJWT.getClaim("id").asLong();

                User user = userUtilityService.getUserById(id);

                CustomUserDetails myUserDetails = new CustomUserDetails(user);
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                myUserDetails,
                                null,
                                myUserDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (SignatureVerificationException sve) {
            handleException(response, new BadRequestError(BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION, Collections.singletonMap("defaultMessage", "Invalid token signature")));
            return;
        } catch (TokenExpiredException tee) {
            handleException(response, new UnAuthorizedError(UnAuthorizedError.ErrorCode.AUTHENTICATION_FAILED, Collections.singletonMap("defaultMessage", "JWT has expired")));
            return;
        } catch (Exception e) {
            handleException(response, new InternalServerError(InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR, Collections.singletonMap("defaultMessage", "An unexpected error occurred")));
            return;
        }

        chain.doFilter(request, response);
    }
    // 인증이 필요하지 않는 url
    private boolean isNonProtectedUrl(HttpServletRequest request) {
        for (String urlPattern : PUBLIC_URLS) {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(urlPattern);
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    private void handleException(HttpServletResponse response, ApiException exception) throws IOException {
        ApiUtils.ApiResult<?> apiResult = exception.body();
        response.setStatus(exception.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(apiResult.toString());
    }
}
