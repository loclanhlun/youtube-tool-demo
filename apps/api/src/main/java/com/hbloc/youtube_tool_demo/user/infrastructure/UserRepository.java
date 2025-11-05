package com.hbloc.youtube_tool_demo.user.infrastructure;

import com.hbloc.youtube_tool_demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
