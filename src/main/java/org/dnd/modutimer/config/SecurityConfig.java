package org.dnd.modutimer.config;


import java.util.Collections;
import org.dnd.modutimer.common.exception.ForbiddenError;
import org.dnd.modutimer.common.security.CustomAuthenticationEntryPoint;
import org.dnd.modutimer.common.security.JwtAuthenticationFilter;
import org.dnd.modutimer.user.application.UserFindService;
import org.dnd.modutimer.utils.FilterResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${frontend.localurl}")
    private String frontlocalurl;

    @Value("${frontend.produrl}")
    private String prodfronturl;

    @Autowired
    private UserFindService userUtilityService;

    public static final String[] PUBLIC_URLS = {
        // swaggger url
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        // open url
        "/api/v1/users/**",
        //h2-console
        "/h2-console/**"

    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
        throws Exception {
        // CSRF 해제
        http.csrf(csrf -> csrf.disable());

        // iframe 허용 : h2-console 사용 목적
        http.headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.disable())
        );

        // cors 재설정
        http.cors(cors -> cors.configurationSource(configurationSource()));

        // jSessionId 사용 거부 (토큰 인증 방식 사용)
        http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // form 로그인 해제 (UsernamePasswordAuthenticationFilter 비활성화)
        http.formLogin(form -> form.disable());

        // 로그인 인증창이 뜨지 않게 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());

        // JwtAuthenticationFilter 추가
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,
            userUtilityService);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 인증 실패, 권한 실패 처리
        http.exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint()) // 인증 실패 처리
            .accessDeniedHandler(((request, response, accessDeniedException) -> {
                // 접근 거부 처리
                FilterResponseUtils.forbidden(response, new ForbiddenError(
                    ForbiddenError.ErrorCode.ROLE_BASED_ACCESS_ERROR,
                    Collections.singletonMap("access", "Access is denied")
                ));
            })));

        // 인증, 권한 필터 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll() // 인증 없이 접근 허용

//                .anyRequest().authenticated()
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }

    // WebSecurityCustomizer를 통해 WebSecurity를 커스터마이징
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization"); // 권고사항

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 개발 환경용 CORS 설정
    @Bean
    @Profile("!prod")
    public CorsConfigurationSource devCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin(frontlocalurl);
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 운영 환경용 CORS 설정
    @Bean
    @Profile("prod")
    public CorsConfigurationSource prodCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        // USER, OWNER 배포 주소 (React)
        configuration.addAllowedOriginPattern(prodfronturl);
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
