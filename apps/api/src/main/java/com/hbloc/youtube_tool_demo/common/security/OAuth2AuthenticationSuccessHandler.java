package com.hbloc.youtube_tool_demo.common.security;

import com.hbloc.youtube_tool_demo.user.domain.RoleEntity;
import com.hbloc.youtube_tool_demo.user.domain.UserEntity;
import com.hbloc.youtube_tool_demo.user.infrastructure.RoleRepository;
import com.hbloc.youtube_tool_demo.user.infrastructure.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String redirectUrl = "https://your-frontend-app.com/oauth2/success?token=";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException, ServletException {
        var oauth2User = (OAuth2User) authentication.getAuthorities();

        String email = (String) oauth2User.getAttributes().get("email");
        RoleEntity role = roleRepository.findByCode("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found"));
        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setStatusId(1);
                    newUser.setRoles(Set.of(role));
                    return userRepository.save(newUser);
                });
        String token = jwtService.generate(email, user.getRoles().stream().map(RoleEntity::getCode).toList());

        String url = redirectUrl + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(url);
    }
}
