package com.hbloc.youtube_tool_demo.user.infrastructure;

import com.hbloc.youtube_tool_demo.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    Optional<UserStatus> findByCode(String code);
}
