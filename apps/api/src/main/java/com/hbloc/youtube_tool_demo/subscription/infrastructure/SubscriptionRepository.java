package com.hbloc.youtube_tool_demo.subscription.infrastructure;

import com.hbloc.youtube_tool_demo.subscription.domain.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
}
