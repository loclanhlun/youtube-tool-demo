package com.hbloc.youtube_tool_demo.user.infrastructure;

import com.hbloc.youtube_tool_demo.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
}
