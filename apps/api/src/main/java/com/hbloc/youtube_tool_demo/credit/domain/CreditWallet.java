package com.hbloc.youtube_tool_demo.credit.domain;

import com.hbloc.youtube_tool_demo.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credit_wallets")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CreditWallet {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "balance_credits", nullable = false)
    private Long balanceCredits;

    @Column(name = "free_credits", nullable = false)
    private Long freeCredits;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "reset_day", nullable = false)
    private Integer resetDay;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


}
