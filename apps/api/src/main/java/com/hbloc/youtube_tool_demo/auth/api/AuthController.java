package com.hbloc.youtube_tool_demo.auth.api;

import com.hbloc.youtube_tool_demo.auth.api.modal.LoginRequest;
import com.hbloc.youtube_tool_demo.auth.application.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(Map.of("accessToken", authService.register(request.getEmail(), request.getPassword())));
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(Map.of("accessToken", authService.login(request.getEmail(), request.getPassword())));
    }

}
