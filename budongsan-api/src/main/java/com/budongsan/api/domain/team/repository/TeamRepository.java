package com.budongsan.api.domain.team.repository;

import com.budongsan.core.domain.member.Member;
import com.budongsan.core.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 팀 Repository
 */
public interface TeamRepository extends JpaRepository<Team, Long> {

    // 초대코드로 팀 조회 (팀 가입 시 사용)
    Optional<Team> findByInviteCode(String inviteCode);

    // 팀장이 만든 팀 목록 조회
    List<Team> findByLeader(Member leader);
}
