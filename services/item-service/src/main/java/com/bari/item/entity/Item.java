package com.bari.item.entity;

import com.bari.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 아이템 엔티티.
 * soft delete 지원 (BaseTimeEntity.deletedAt 사용)
 */
@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 아이템 이름 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 아이템 설명 (선택사항) */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 가격 */
    @Column(nullable = false)
    private int price;

    /**
     * 생성자 ID — api-gateway에서 주입된 X-User-Id 헤더 값.
     * user-service를 직접 호출하지 않고 헤더로 받은 값을 저장합니다.
     */
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    // ========== 정적 팩토리 ==========

    /**
     * 새 아이템 생성.
     *
     * @param name        아이템 이름
     * @param description 아이템 설명
     * @param price       가격
     * @param createdBy   생성자 userId (X-User-Id 헤더에서 추출)
     * @return 생성된 Item 엔티티
     */
    public static Item create(String name, String description, int price, Long createdBy) {
        Item item = new Item();
        item.name = name;
        item.description = description;
        item.price = price;
        item.createdBy = createdBy;
        return item;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 아이템 정보 수정.
     * 본인 소유 아이템만 수정 가능 (서비스 레이어에서 권한 체크).
     *
     * @param name        새 이름
     * @param description 새 설명
     * @param price       새 가격
     */
    public void update(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
