package com.bari.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 공통 시간 필드를 제공하는 기본 클래스.
 * createdAt: 생성 시간 (자동 설정)
 * deletedAt: 삭제 시간 (soft delete 용, null이면 삭제되지 않은 상태)
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    /**
     * 엔티티 생성 시간 — JPA Auditing으로 자동 설정
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * soft delete 용 삭제 시간.
     * null이면 정상 상태, 값이 있으면 삭제된 상태.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * soft delete 처리 — 현재 시간으로 deletedAt 설정
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * soft delete 복구 — deletedAt을 null로 초기화
     */
    public void restore() {
        this.deletedAt = null;
    }
}
