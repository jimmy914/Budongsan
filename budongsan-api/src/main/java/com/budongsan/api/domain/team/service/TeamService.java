package com.budongsan.api.domain.team.service;

import com.budongsan.api.domain.member.repository.MemberRepository;
import com.budongsan.api.domain.team.dto.TeamCreateRequest;
import com.budongsan.api.domain.team.dto.TeamResponse;
import com.budongsan.api.domain.team.repository.TeamRepository;
import com.budongsan.core.domain.member.Member;
import com.budongsan.core.domain.member.MemberRole;
import com.budongsan.core.domain.team.Team;
import com.budongsan.core.exception.BusinessException;
import com.budongsan.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    /**
     * 팀 생성 (LEADER만 가능)
     * 팀장은 여러 팀 생성 가능
     */
    @Transactional
    public TeamResponse createTeam(String leaderEmail, TeamCreateRequest request) {
        Member leader = memberRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // LEADER 권한 체크
        if (leader.getRole() != MemberRole.LEADER) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Team team = Team.builder()
                .name(request.name())
                .leader(leader)
                .build();

        return TeamResponse.from(teamRepository.save(team));
    }

    /**
     * 내 팀 목록 조회
     * - LEADER: 내가 만든 팀 목록
     * - MEMBER: 내가 속한 팀
     */
    @Transactional(readOnly = true)
    public List<TeamResponse> getMyTeams(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getRole() == MemberRole.LEADER) {
            // 팀장: 내가 만든 팀 목록
            return teamRepository.findByLeader(member)
                    .stream()
                    .map(TeamResponse::from)
                    .toList();
        } else {
            // 팀원: 내가 속한 팀
            if (member.getTeam() == null) return List.of();
            return List.of(TeamResponse.from(member.getTeam()));
        }
    }

    /**
     * 팀 가입 (MEMBER만 가능, 초대코드 입력)
     */
    @Transactional
    public void joinTeam(String memberEmail, String inviteCode) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // MEMBER 권한 체크
        if (member.getRole() != MemberRole.MEMBER) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 이미 팀에 속해있으면 거부
        if (member.getTeam() != null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        // 초대코드로 팀 조회
        Team team = teamRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND));

        member.joinTeam(team);
    }

    /**
     * 팀원 목록 조회 (LEADER만 가능)
     */
    @Transactional(readOnly = true)
    public List<String> getTeamMembers(String leaderEmail, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND));

        // 팀장 본인 팀인지 확인
        if (!team.getLeader().getEmail().equals(leaderEmail)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return team.getMembers().stream()
                .map(m -> m.getName() + " (" + m.getEmail() + ")")
                .toList();
    }
}
