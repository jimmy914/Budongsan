package com.budongsan.core.domain.member;

/**
 * 회원 권한 enum
 *
 * ADMIN  → 시스템 전체 관리자 (모든 팀/회원 관리)
 * LEADER → 팀장 (팀 생성, 여러 팀 관리 가능)
 * MEMBER → 팀원 (1개 팀만 가입 가능)
 */
public enum MemberRole {
    ADMIN, LEADER, MEMBER
}
