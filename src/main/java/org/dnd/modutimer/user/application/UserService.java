package org.dnd.modutimer.user.application;


import lombok.RequiredArgsConstructor;
import org.dnd.modutimer.common.exception.BadRequestError;
import org.dnd.modutimer.common.exception.InternalServerError;
import org.dnd.modutimer.common.exception.NotFoundError;
import org.dnd.modutimer.common.exception.UnAuthorizedError;
import org.dnd.modutimer.common.security.JWTProvider;
import org.dnd.modutimer.user.domain.User;
import org.dnd.modutimer.user.domain.UserRepository;
import org.dnd.modutimer.user.dto.UserLoginRequest;
import org.dnd.modutimer.user.dto.UserLoginResponse;
import org.dnd.modutimer.user.dto.UserRegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void register(UserRegisterRequest userRegisterRequest) {
        checkSameEmail(userRegisterRequest.getEmail());

        String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
        try {
            userRepository.save(userRegisterRequest.toEntity(encodedPassword));
        } catch (Exception e) {
            throw new InternalServerError(
                InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR,
                Collections.singletonMap("error", "Unknown server error occurred."));
        }
    }

    public void checkSameEmail(String email) {
        Optional<User> memberOptional = userRepository.findByEmail(email);
        if (memberOptional.isPresent()) {
            throw new BadRequestError(BadRequestError.ErrorCode.DUPLICATE_RESOURCE,
                Collections.singletonMap("Email", "Duplicate email exist : " + email));
        }
    }

    public UserLoginResponse login(UserLoginRequest requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
            () -> new NotFoundError(
                NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("Email", "email not found : " + requestDTO.getEmail())
            ));
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new UnAuthorizedError(
                UnAuthorizedError.ErrorCode.AUTHENTICATION_FAILED,
                Collections.singletonMap("Password", "Wrong password")
            );
        }

        String jwt = JWTProvider.create(user);
        String redirectUrl = "/user/home";

        return new UserLoginResponse(jwt, redirectUrl);
    }

//    public UserInfoResponse findUser(User user) {
//        User findUser = userRepository.findById(user.getId())
//                .orElseThrow(() -> new NotFoundError(
//                        NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
//                        Collections.singletonMap("UserId", "User is not found.")
//                ));
//
//        return UserInfoResponse.from(findUser);
//    }
}