package com.hbloc.youtube_tool_demo.user.infrastructure;

import com.hbloc.youtube_tool_demo.user.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByCode(String code);
}
