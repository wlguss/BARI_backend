package com.bari.inventory.entity;

import java.time.LocalDateTime;

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
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Integer quantity;

    private LocalDateTime expireAt;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    public void update(Integer quantity, LocalDateTime expireAt) {
        this.quantity = quantity;
        this.expireAt = expireAt;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expireAt.isBefore(LocalDateTime.now());
    }
}