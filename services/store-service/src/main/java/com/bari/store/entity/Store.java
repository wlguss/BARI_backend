package com.bari.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter 
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stores")
@EntityListeners(AuditingEntityListener.class)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String storeName;

    // 매장 상세 페이지 소개글
    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String address;

    // 매장 전화번호 (프론트 StoreDetail에서 사용)
    @Column(length = 20)
    private String phone;

    // 영업 시간 (예: "09:00 - 22:00")
    @Column(name = "business_hours")
    private String businessHours;

    // 카테고리
    private String category;

    // 매장 대표 이미지 URL
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}