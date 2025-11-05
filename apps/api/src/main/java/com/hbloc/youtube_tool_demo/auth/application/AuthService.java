package com.hbloc.youtube_tool_demo.auth.application;

import com.hbloc.youtube_tool_demo.common.security.JwtService;
import com.hbloc.youtube_tool_demo.user.domain.User;
import com.hbloc.youtube_tool_demo.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email already exists");

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return jwtService.generate(email, List.of("ROLE_USER"));
    }

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatusId() == 2 || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var roles = user.getRoles().stream().map(r -> "ROLE_" + r.getCode()).toList();

        return jwtService.generate(email, roles.isEmpty() ? List.of("ROLE_USER"): roles);
    }
}