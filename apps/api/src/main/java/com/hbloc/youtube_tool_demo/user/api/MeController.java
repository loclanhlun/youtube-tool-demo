package com.hbloc.youtube_tool_demo.user.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Map<String,String> profile(Authentication auth) {
        return Map.of("email", auth.getName());
    }
}
