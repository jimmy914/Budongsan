package com.budongsan.core.domain.member;

import com.budongsan.core.domain.team.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 엔티티 - DB의 member 테이블과 매핑
 *
 * @Entity    → JPA가 이 클래스를 DB 테이블로 관리
 * @Getter    → Lombok이 모든 필드의 getter 자동 생성
 * @NoArgsConstructor(AccessLevel.PROTECTED)
 *   → 기본 생성자를 protected로 제한 (JPA 스펙 + 외부에서 new Member() 방지)
 *   → 객체 생성은 반드시 Builder로만!
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt 암호화된 비밀번호

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // DB에 "ADMIN", "LEADER", "MEMBER" 문자열로 저장
    @Column(nullable = false)
    private MemberRole role;

    // 팀원만 팀에 소속됨 (팀장은 Team 엔티티에서 leader로 관리)
    // nullable = true → 팀 미가입 상태 허용
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 (필요할 때만 팀 정보 조회)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist // DB 저장 직전 자동 실행
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Member(String email, String password, String name, MemberRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // 팀 가입 (팀원만 사용)
    public void joinTeam(Team team) {
        this.team = team;
    }

    // 팀 탈퇴
    public void leaveTeam() {
        this.team = null;
    }
}
