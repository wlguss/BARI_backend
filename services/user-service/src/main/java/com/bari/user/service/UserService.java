package com.bari.user.service;

import com.bari.common.exception.BusinessException;
import com.bari.security.jwt.JwtTokenProvider;
import com.bari.user.client.StoreInfo;
import com.bari.user.client.StoreServiceClient;
import com.bari.user.dto.request.LoginRequest;
import com.bari.user.dto.request.SignUpRequest;
import com.bari.user.dto.response.LoginResponse;
import com.bari.user.dto.response.UserResponse;
import com.bari.user.entity.User;
import com.bari.user.entity.UserRole;
import com.bari.user.exception.UserErrorCode;
import com.bari.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 사용자 비즈니스 로직 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StoreServiceClient storeServiceClient;

    /**
     * RedisTemplate<String, String>: key-value 모두 String 타입
     * Refresh Token 저장: "refresh:{userId}" → refreshToken
     */
    private final RedisTemplate<String, String> redisTemplate;

    /** Redis에 저장되는 Refresh Token 키 접두사 */
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    /** Refresh Token 만료 시간 (7일, Redis TTL) */
    private static final long REFRESH_TOKEN_TTL_DAYS = 7L;

    /**
     * 회원가입.
     * 이메일 중복 체크 후 비밀번호를 BCrypt로 인코딩하여 저장합니다.
     *
     * @param request 회원가입 요청 DTO
     * @return 생성된 사용자 정보
     * @throws BusinessException EMAIL_DUPLICATED — 이미 사용 중인 이메일
     */
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(UserErrorCode.EMAIL_DUPLICATED);
        }

        // 비밀번호 BCrypt 인코딩
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성 및 저장
        User user = User.create(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getRole()
        );

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료 - userId: {}, email: {}", savedUser.getId(), savedUser.getEmail());

        return UserResponse.from(savedUser);
    }

    /**
     * 로그인.
     * 이메일/비밀번호 검증 후 JWT 토큰을 발급합니다.
     * Refresh Token은 Redis에 저장됩니다 (key: "refresh:{userId}").
     *
     * @param request 로그인 요청 DTO
     * @return Access Token, Refresh Token, 사용자 정보
     * @throws BusinessException USER_NOT_FOUND — 가입되지 않은 이메일
     * @throws BusinessException INVALID_PASSWORD — 비밀번호 불일치
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 이메일로 사용자 조회 (soft delete 미포함)
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }

        // JWT 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Refresh Token을 Redis에 저장 (TTL: 7일)
        String redisKey = REFRESH_TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(redisKey, refreshToken, REFRESH_TOKEN_TTL_DAYS, TimeUnit.DAYS);

        log.info("로그인 성공 - userId: {}", user.getId());

        // OWNER 역할인 경우 storeId/storeName 조회
        if (user.getRole() == UserRole.OWNER) {
            StoreInfo store = storeServiceClient.getStoreByOwnerId(user.getId());
            if (store != null) {
                return LoginResponse.ofOwner(accessToken, refreshToken, user.getId(), user.getRole(),
                        store.getId(), store.getStoreName());
            }
        }

        return LoginResponse.of(accessToken, refreshToken, user.getId(), user.getRole());
    }

    /**
     * 토큰 갱신.
     * Redis에서 Refresh Token을 검증하고 새 토큰을 발급합니다.
     *
     * @param refreshToken Refresh Token
     * @return 새 Access Token, 새 Refresh Token
     * @throws BusinessException INVALID_TOKEN — 유효하지 않은 토큰
     */
    @Transactional
    public LoginResponse refresh(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String redisKey = REFRESH_TOKEN_PREFIX + userId;

        // Redis에서 저장된 Refresh Token 조회
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 새 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Redis의 Refresh Token 갱신
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, REFRESH_TOKEN_TTL_DAYS, TimeUnit.DAYS);

        log.info("토큰 갱신 성공 - userId: {}", userId);

        // OWNER 역할인 경우 storeId/storeName 조회
        if (user.getRole() == UserRole.OWNER) {
            StoreInfo store = storeServiceClient.getStoreByOwnerId(user.getId());
            if (store != null) {
                return LoginResponse.ofOwner(newAccessToken, newRefreshToken, user.getId(), user.getRole(),
                        store.getId(), store.getStoreName());
            }
        }

        return LoginResponse.of(newAccessToken, newRefreshToken, user.getId(), user.getRole());
    }

    /**
     * 로그아웃.
     * Redis에서 Refresh Token을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void logout(Long userId) {
        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(redisKey);
        log.info("로그아웃 완료 - userId: {}", userId);
    }

    /**
     * 사용자 정보 조회.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     * @throws BusinessException USER_NOT_FOUND — 사용자를 찾을 수 없음
     */
    public UserResponse getUser(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    /**
     * 회원 탈퇴.
     * deletedAt을 현재 시간으로 설정(soft delete)하고 Redis의 Refresh Token을 삭제합니다.
     *
     * @param userId 사용자 ID
     * @throws BusinessException USER_NOT_FOUND — 사용자를 찾을 수 없음
     */
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // soft delete 처리 (deletedAt 업데이트)
        user.softDelete();

        // Redis의 Refresh Token 삭제
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);

        log.info("회원 탈퇴 완료 - userId: {}", userId);
    }
}
