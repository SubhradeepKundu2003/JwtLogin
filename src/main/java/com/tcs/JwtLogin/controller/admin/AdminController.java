package com.tcs.JwtLogin.controller.admin;

import com.tcs.JwtLogin.dto.UserResponse;
import com.tcs.JwtLogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users/not-enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllNotEnabledUsers() {
        return userService.getAllNotEnabledUsers();
    }
}

