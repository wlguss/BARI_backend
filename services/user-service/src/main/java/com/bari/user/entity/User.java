package com.bari.user.entity;

import com.bari.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티.
 * soft delete 지원 (BaseTimeEntity.deletedAt 사용)
 */
@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 이메일 (로그인 ID, 중복 불가) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** BCrypt 인코딩된 비밀번호 */
    @Column(nullable = false, length = 255)
    private String password;

    /** 닉네임 */
    @Column(length = 50)
    private String nickname;

    /** 사용자 권한 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    // ========== 정적 팩토리 ==========

    /**
     * 새 사용자 생성.
     *
     * @param email           이메일
     * @param encodedPassword BCrypt 인코딩된 비밀번호
     * @param nickname        닉네임
     * @param role            사용자 권한
     * @return 생성된 User 엔티티
     */
    public static User create(String email, String encodedPassword, String nickname, UserRole role) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.role = role;
        return user;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 비밀번호 변경.
     *
     * @param encodedPassword BCrypt 인코딩된 새 비밀번호
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
