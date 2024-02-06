package org.dnd.timeet.common.security;


import lombok.RequiredArgsConstructor;
import org.dnd.timeet.user.domain.User;
import org.dnd.timeet.user.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 가입된 유저에 대하여 세션을 생성하고 반환한다.
     *
     * @param email 검색할 사용자의 이메일 주소
     * @return 가입된 유저에 해당하는 세션 정보
     * @throws UsernameNotFoundException 주어진 이메일에 해당하는 유저를 찾을 수 없을 때 발생
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email: %s not found.", email)));
        return new CustomUserDetails(user);
    }

}
