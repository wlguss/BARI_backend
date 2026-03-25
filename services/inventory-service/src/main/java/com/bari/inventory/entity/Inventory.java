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
@Table(name = "inventories")
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
    private Integer price;

    private LocalDateTime expireAt;
    private String memo;

    public void update(Integer quantity, Integer price, LocalDateTime expireAt, String memo) {

        this.quantity = quantity;
        this.price = price;
        this.expireAt = expireAt;
        this.memo = memo;
    }

    public void updateQuantity(int quantity) {
        this.quantity = this.quantity - quantity;
    }

    public boolean isExpired() {
        return this.expireAt.isBefore(LocalDateTime.now());
    }
}