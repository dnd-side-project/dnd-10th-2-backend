package org.dnd.modutimer.user.application;

import org.dnd.modutimer.common.exception.NotFoundError;
import org.dnd.modutimer.user.domain.User;
import org.dnd.modutimer.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserFindService {

    private final UserRepository userRepository;

    @Autowired
    public UserFindService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundError(NotFoundError.ErrorCode.RESOURCE_NOT_FOUND,
                Collections.singletonMap("User", "User not found")));
    }
}