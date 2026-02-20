package com.budongsan.core.domain.team;

import com.budongsan.core.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 팀 엔티티 - DB의 team 테이블과 매핑
 *
 * 팀장(LEADER)은 여러 팀을 만들 수 있음 (1:N)
 * 팀원(MEMBER)은 1개 팀만 가입 가능
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 팀 이름

    @Column(nullable = false, unique = true)
    private String inviteCode; // 초대코드 (UUID 기반, 유일값)

    // 팀장 (Member와 N:1 관계 - 한 팀장이 여러 팀 생성 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Member leader;

    // 팀에 속한 팀원 목록 (1:N 관계)
    // mappedBy = "team" → Member.team 필드가 연관관계 주인
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // 초대코드 자동 생성 (UUID 앞 8자리만 사용, ex: "a1b2c3d4")
        this.inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Builder
    public Team(String name, Member leader) {
        this.name = name;
        this.leader = leader;
    }
}
