package com.bari.store.entity;

import com.bari.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stores")
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "business_hours")
    private String businessHours;

    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // [추가] 소프트 삭제 메서드
    public void delete() {
        this.softDelete();
    }

    // [추가] 정보 업데이트 메서드 (서비스 로직에서 사용하기 좋음)
    public void updateInfo(String name, String description, String address, String phone, 
                          String businessHours, String category, String imageUrl) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.businessHours = businessHours;
        this.category = category;
        this.imageUrl = imageUrl;
    }
}