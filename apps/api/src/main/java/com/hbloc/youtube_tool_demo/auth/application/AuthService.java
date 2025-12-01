package com.hbloc.youtube_tool_demo.auth.application;

import com.hbloc.youtube_tool_demo.common.security.JwtService;
import com.hbloc.youtube_tool_demo.user.domain.RoleEntity;
import com.hbloc.youtube_tool_demo.user.domain.UserEntity;
import com.hbloc.youtube_tool_demo.user.infrastructure.RoleRepository;
import com.hbloc.youtube_tool_demo.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email already exists");

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatusId(1);
        RoleEntity role =  roleRepository.findByCode("ROLE_USER").orElseThrow(() -> new RuntimeException("Role is not exist"));
        user.setRoles(Set.of(role));
        userRepository.save(user);



        return jwtService.generate(email, List.of("ROLE_USER"));
    }

    @Override
    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatusId() == 2 || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var roles = user.getRoles().stream().map(RoleEntity::getCode).toList();

        return jwtService.generate(email, roles.isEmpty() ? List.of("ROLE_USER"): roles);
    }
}
