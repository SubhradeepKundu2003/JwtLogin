package com.tcs.JwtLogin.service;

import com.tcs.JwtLogin.dto.UserResponse;
import com.tcs.JwtLogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllNotEnabledUsers() {
        return userRepository.findByEnabledFalse()
                .stream()
                .map(UserResponse::from)
                .toList();
    }
}
