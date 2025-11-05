package com.hbloc.youtube_tool_demo.user.domain;

import com.hbloc.youtube_tool_demo.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_status")
@Getter
@Setter
public class UserStatus extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;
}
