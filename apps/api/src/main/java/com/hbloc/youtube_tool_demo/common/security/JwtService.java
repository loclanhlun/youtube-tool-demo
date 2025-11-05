package com.hbloc.youtube_tool_demo.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-token-ttl-minutes}")
    private long ttlMinutes;

    public String generate(String subject, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttlMinutes, ChronoUnit.MINUTES)))
                .claim("roles", roles)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Authentication toAuthentication(String token) {
        try {
           var jws = Jwts.parser()
                   .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                   .build()
                   .parseSignedClaims(token);
           String username = jws.getPayload().getSubject();

            List<String> roles = (List<String>) jws.getPayload().get("roles");
            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } catch (JwtException e) {
            return null;
        }
    }
}
