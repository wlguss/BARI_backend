package com.bari.inventory.entity;

import java.time.LocalDateTime;

import com.bari.common.entity.BaseTimeEntity;
import com.bari.inventory.dto.request.InventoryUpdateRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Integer quantity;

    private LocalDateTime expireAt;

    public void update(InventoryUpdateRequest dto) {
        this.quantity = dto.getQuantity();
        this.expireAt = dto.getExpireAt();
    }

    public boolean isExpired() {
        return this.expireAt.isBefore(LocalDateTime.now());
    }
}