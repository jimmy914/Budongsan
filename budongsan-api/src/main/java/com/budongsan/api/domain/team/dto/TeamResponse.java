package com.budongsan.api.domain.team.dto;

import com.budongsan.core.domain.team.Team;

/**
 * 팀 응답 DTO
 */
public record TeamResponse(
        Long id,
        String name,
        String inviteCode,
        String leaderName,
        int memberCount
) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getInviteCode(),
                team.getLeader().getName(),
                team.getMembers().size()
        );
    }
}
