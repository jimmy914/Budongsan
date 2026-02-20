package com.budongsan.api.domain.team.controller;

import com.budongsan.api.domain.team.dto.TeamCreateRequest;
import com.budongsan.api.domain.team.dto.TeamResponse;
import com.budongsan.api.domain.team.service.TeamService;
import com.budongsan.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Team", description = "팀 관리 API")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "팀 생성", description = "팀장만 팀을 생성할 수 있습니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TeamCreateRequest request) {
        TeamResponse response = teamService.createTeam(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("팀이 생성되었습니다.", response));
    }

    @Operation(summary = "내 팀 조회", description = "팀장: 내가 만든 팀 목록 / 팀원: 내가 속한 팀")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getMyTeams(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<TeamResponse> response = teamService.getMyTeams(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "팀 가입", description = "초대코드로 팀에 가입합니다. (팀원만 가능)")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> joinTeam(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String inviteCode) {
        teamService.joinTeam(userDetails.getUsername(), inviteCode);
        return ResponseEntity.ok(ApiResponse.success("팀에 가입되었습니다."));
    }

    @Operation(summary = "팀원 목록 조회", description = "팀장만 팀원 목록을 조회할 수 있습니다.")
    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<List<String>>> getTeamMembers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long teamId) {
        List<String> members = teamService.getTeamMembers(userDetails.getUsername(), teamId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }
}
