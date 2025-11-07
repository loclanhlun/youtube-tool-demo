package com.hbloc.youtube_tool_demo.user.infrastructure;

import com.hbloc.youtube_tool_demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
