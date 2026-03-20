package com.bari.user.repository;

import com.bari.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 레포지토리.
 * soft delete를 고려해서 deletedAt IS NULL 조건을 포함합니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회 (삭제되지 않은 사용자만).
     * 로그인 시 사용합니다.
     *
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    /**
     * 이메일 중복 여부 확인 (삭제된 사용자 포함).
     * 회원가입 시 이메일 중복 체크에 사용합니다.
     *
     * @param email 이메일
     * @return 중복이면 true
     */
    boolean existsByEmail(String email);

    /**
     * ID로 사용자 조회 (삭제되지 않은 사용자만).
     * 내 정보 조회, 회원 탈퇴 시 사용합니다.
     *
     * @param id 사용자 ID
     * @return 사용자 Optional
     */
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
}
