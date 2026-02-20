package com.budongsan.api.domain.member.repository;

import com.budongsan.core.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 Repository
 *
 * JpaRepository<Member, Long> 상속만으로
 * save(), findById(), findAll(), delete() 등 기본 CRUD 자동 제공!
 *
 * 추가로 필요한 쿼리는 메서드 이름으로 자동 생성됨
 * ex) findByEmail → SELECT * FROM member WHERE email = ?
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 조회 (로그인 시 사용)
    Optional<Member> findByEmail(String email);

    // 이메일 중복 체크 (회원가입 시 사용)
    boolean existsByEmail(String email);
}
