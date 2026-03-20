package com.bari.discount.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "discount")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long inventoryId;

    private Integer originalPrice;
    private Integer discountPrice;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 할인 수정
    public void update(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    // 할인 종료
    public void end() {
        this.endAt = LocalDateTime.now();
    }

    // soft delete
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}