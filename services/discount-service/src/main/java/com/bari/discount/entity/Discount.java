package com.bari.discount.entity;

import java.time.LocalDateTime;

import com.bari.common.entity.BaseTimeEntity;

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
@Table(name = "discounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Discount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long inventoryId;

    private Integer originalPrice;
    private Integer discountPrice;
    private Integer discountRate;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    // 할인 수정
    public void update(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    // 할인 종료
    public void end() {
        this.endAt = LocalDateTime.now();
    }

}